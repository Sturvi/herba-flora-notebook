package com.example.inovasiyanotebook.service.viewservices.common;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для ленивых DataProvider'ов Vaadin поверх Spring Data {@link Specification}.
 * <p>
 * Сортировка задаётся через Specification, а не через {@link Pageable}: SimpleJpaRepository
 * применяет Sort из Pageable поверх Specification и перезаписал бы ORDER BY.
 */
public final class GridQuerySupport {

    private GridQuerySupport() {
    }

    /**
     * Ключ сортировки: JPA-путь ("orderNo", "product.name"), направление и где ставить NULL.
     */
    public record SortKey(String path, boolean ascending, boolean nullsFirst) {
        public static SortKey asc(String path) {
            return new SortKey(path, true, false);
        }

        public static SortKey desc(String path) {
            return new SortKey(path, false, false);
        }
    }

    /**
     * Pageable без Sort. Vaadin выравнивает offset по pageSize, поэтому номер страницы корректен.
     */
    public static Pageable toPageable(Query<?, ?> query) {
        return PageRequest.of(query.getPage(), query.getPageSize());
    }

    /**
     * Vaadin присылает пустой sortOrders, когда пользователь снял сортировку, — тогда используется defaultSort.
     * Ключ сортировки колонки ({@code setSortProperty}) должен быть JPA-путём.
     */
    public static List<SortKey> toSortKeys(List<QuerySortOrder> vaadinOrders, List<SortKey> defaultSort) {
        if (vaadinOrders == null || vaadinOrders.isEmpty()) {
            return defaultSort;
        }
        return vaadinOrders.stream()
                .map(order -> new SortKey(order.getSorted(), order.getDirection() == SortDirection.ASCENDING, false))
                .toList();
    }

    /**
     * Specification, которая только выставляет ORDER BY (предикат не добавляет).
     * Для count-запроса (result type Long) ничего не делает.
     * Всегда добавляет tie-breaker {@code id DESC} для стабильной пагинации.
     */
    public static <T> Specification<T> orderBy(List<SortKey> keys) {
        return (root, query, cb) -> {
            Class<?> resultType = query.getResultType();
            if (Long.class.equals(resultType) || long.class.equals(resultType)) {
                return null;
            }
            List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
            for (SortKey key : keys) {
                orders.add(toOrder(root, cb, key));
            }
            orders.add(cb.desc(root.get("id")));
            query.orderBy(orders);
            return null;
        };
    }

    private static jakarta.persistence.criteria.Order toOrder(Root<?> root, CriteriaBuilder cb, SortKey key) {
        Expression<?> expression = resolvePath(root, key.path());
        if (cb instanceof HibernateCriteriaBuilder hcb) {
            return key.ascending() ? hcb.asc(expression, key.nullsFirst()) : hcb.desc(expression, key.nullsFirst());
        }
        return key.ascending() ? cb.asc(expression) : cb.desc(expression);
    }

    /**
     * "product.name" -> LEFT JOIN product, затем атрибут name. Join переиспользуется, если уже создан фильтром.
     */
    public static Expression<?> resolvePath(From<?, ?> from, String path) {
        String[] parts = path.split("\\.");
        From<?, ?> current = from;
        for (int i = 0; i < parts.length - 1; i++) {
            current = getOrCreateLeftJoin(current, parts[i]);
        }
        return current.get(parts[parts.length - 1]);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> Join<X, Y> getOrCreateLeftJoin(From<?, X> from, String attribute) {
        for (Join<X, ?> join : from.getJoins()) {
            if (join.getAttribute().getName().equals(attribute) && join.getJoinType() == JoinType.LEFT) {
                return (Join<X, Y>) join;
            }
        }
        return from.join(attribute, JoinType.LEFT);
    }

    /**
     * "%term%" в нижнем регистре или null, если строка пустая.
     */
    public static String likePattern(String term) {
        if (term == null) {
            return null;
        }
        String trimmed = term.trim().toLowerCase();
        return trimmed.isEmpty() ? null : "%" + trimmed + "%";
    }
}
