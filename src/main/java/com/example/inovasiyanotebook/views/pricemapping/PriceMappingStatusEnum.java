package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductPriceMapping;

import java.util.function.Predicate;

/**
 * Статусы сопоставления позиций прайса с продуктами.
 */
public enum PriceMappingStatusEnum {
    TO_BE_MAPPED("Eyniləşdirilməmiş", mapping -> mapping.getProduct() == null && !mapping.isIgnored()),
    ALREADY_MAPPED("Eyniləşdirilmişlər", mapping -> mapping.getProduct() != null && !mapping.isIgnored()),
    IGNORED("Nəzərə alınmayanlar", ProductPriceMapping::isIgnored),
    ALL("Hamısı", mapping -> true);

    private final String displayName;
    private final Predicate<ProductPriceMapping> matcher;

    PriceMappingStatusEnum(String displayName, Predicate<ProductPriceMapping> matcher) {
        this.displayName = displayName;
        this.matcher = matcher;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matches(ProductPriceMapping mapping) {
        return matcher.test(mapping);
    }
}
