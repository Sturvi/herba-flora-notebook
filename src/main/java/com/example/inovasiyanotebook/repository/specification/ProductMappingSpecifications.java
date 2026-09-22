package com.example.inovasiyanotebook.repository.specification;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.OrderMappingStatusEnum;
import org.springframework.data.jpa.domain.Specification;

/**
 * Условия выборки сопоставлений позиций 1C для грида: статус сопоставления и поиск.
 */
public final class ProductMappingSpecifications {

    private ProductMappingSpecifications() {
    }

    public record ProductMappingGridFilter(OrderMappingStatusEnum status, String term) {
    }

    public static Specification<ProductMapping> forGrid(ProductMappingGridFilter filter) {
        return Specification.where(byStatus(filter.status()))
                .and(matchesTerm(filter.term()));
    }

    static Specification<ProductMapping> byStatus(OrderMappingStatusEnum status) {
        if (status == null || status == OrderMappingStatusEnum.ALL) {
            return null;
        }
        return (root, query, cb) -> status == OrderMappingStatusEnum.TO_BE_MAPPED
                ? cb.or(cb.isNull(root.get("product")), cb.isNull(root.get("printedType")))
                : cb.and(cb.isNotNull(root.get("product")), cb.isNotNull(root.get("printedType")));
    }

    static Specification<ProductMapping> matchesTerm(String term) {
        String pattern = GridQuerySupport.likePattern(term);
        if (pattern == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("incomingOrderPositionName")), pattern),
                cb.like(cb.lower(GridQuerySupport.getOrCreateLeftJoin(root, "product").get("name")), pattern),
                cb.like(cb.lower(GridQuerySupport.getOrCreateLeftJoin(root, "printedType").get("name")), pattern));
    }
}
