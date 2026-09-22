package com.example.inovasiyanotebook.repository.specification;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import com.example.inovasiyanotebook.views.pricemapping.PriceMappingStatusEnum;
import org.springframework.data.jpa.domain.Specification;

/**
 * Условия выборки сопоставлений позиций прайса для грида: статус сопоставления и поиск.
 * Условия статуса повторяют {@link PriceMappingStatusEnum#matches}.
 */
public final class ProductPriceMappingSpecifications {

    private ProductPriceMappingSpecifications() {
    }

    public record PriceMappingGridFilter(PriceMappingStatusEnum status, String term) {
    }

    public static Specification<ProductPriceMapping> forGrid(PriceMappingGridFilter filter) {
        return Specification.where(byStatus(filter.status()))
                .and(matchesTerm(filter.term()));
    }

    static Specification<ProductPriceMapping> byStatus(PriceMappingStatusEnum status) {
        if (status == null || status == PriceMappingStatusEnum.ALL) {
            return null;
        }
        return (root, query, cb) -> switch (status) {
            case TO_BE_MAPPED -> cb.and(cb.isNull(root.get("product")), cb.isFalse(root.get("ignored")));
            case ALREADY_MAPPED -> cb.and(cb.isNotNull(root.get("product")), cb.isFalse(root.get("ignored")));
            case IGNORED -> cb.isTrue(root.get("ignored"));
            default -> cb.conjunction();
        };
    }

    static Specification<ProductPriceMapping> matchesTerm(String term) {
        String pattern = GridQuerySupport.likePattern(term);
        if (pattern == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("incomingOrderPositionName")), pattern),
                cb.like(cb.lower(GridQuerySupport.getOrCreateLeftJoin(root, "product").get("name")), pattern));
    }
}
