package com.example.inovasiyanotebook.repository.specification;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Условия выборки заказов для грида: статус, поиск, ограничение по продукту.
 */
public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    /**
     * @param status    null - все статусы
     * @param term      строка поиска (номер заказа, имя продукта в позициях, локализованное имя статуса)
     * @param productId null - без ограничения по продукту
     */
    public record OrderGridFilter(OrderStatusEnum status, String term, Long productId) {
    }

    public static Specification<Order> forGrid(OrderGridFilter filter) {
        return Specification.where(statusIs(filter.status()))
                .and(hasProduct(filter.productId()))
                .and(matchesTerm(filter.term()));
    }

    static Specification<Order> statusIs(OrderStatusEnum status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /**
     * EXISTS (SELECT 1 FROM OrderPosition op WHERE op.order = o AND op.product.id = :productId)
     */
    static Specification<Order> hasProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<OrderPosition> position = subquery.from(OrderPosition.class);
            subquery.select(cb.literal(1L)).where(
                    cb.equal(position.get("order"), root),
                    cb.equal(position.get("product").get("id"), productId));
            return cb.exists(subquery);
        };
    }

    /**
     * Номер заказа как строка LIKE %term%, либо есть позиция с продуктом LIKE %term%,
     * либо статус среди тех, чьё локализованное имя содержит term.
     */
    static Specification<Order> matchesTerm(String term) {
        String pattern = GridQuerySupport.likePattern(term);
        if (pattern == null) {
            return null;
        }
        String lower = term.trim().toLowerCase();
        Set<OrderStatusEnum> statusesByName = Arrays.stream(OrderStatusEnum.values())
                .filter(status -> status.getName().toLowerCase().contains(lower))
                .collect(Collectors.toSet());

        return (root, query, cb) -> {
            List<Predicate> any = new ArrayList<>();
            any.add(cb.like(root.get("orderNo").as(String.class), pattern));

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<OrderPosition> position = subquery.from(OrderPosition.class);
            Join<OrderPosition, Product> product = position.join("product");
            subquery.select(cb.literal(1L)).where(
                    cb.equal(position.get("order"), root),
                    cb.like(cb.lower(product.get("name")), pattern));
            any.add(cb.exists(subquery));

            if (!statusesByName.isEmpty()) {
                any.add(root.get("status").in(statusesByName));
            }
            return cb.or(any.toArray(Predicate[]::new));
        };
    }
}
