package com.example.inovasiyanotebook.repository.specification;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import org.springframework.data.jpa.domain.Specification;

/**
 * Условия выборки продуктов для грида: клиент, категория (с подкатегориями), поиск.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    /**
     * @param client   null - все клиенты
     * @param category null - все категории; иначе продукты категории и её подкатегорий
     * @param term     поиск по имени продукта, категории и клиента
     */
    public record ProductGridFilter(Client client, Category category, String term) {
    }

    public static Specification<Product> forGrid(ProductGridFilter filter) {
        return Specification.where(byClient(filter.client()))
                .and(byCategoryOrSubcategory(filter.category()))
                .and(matchesTerm(filter.term()));
    }

    static Specification<Product> byClient(Client client) {
        if (client == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("client"), client);
    }

    /**
     * p.category = :c OR p.category.parentCategory = :c (аналог findAllByCategoryAndHisSubCategory).
     */
    static Specification<Product> byCategoryOrSubcategory(Category category) {
        if (category == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("category"), category),
                cb.equal(GridQuerySupport.getOrCreateLeftJoin(root, "category").get("parentCategory"), category));
    }

    static Specification<Product> matchesTerm(String term) {
        String pattern = GridQuerySupport.likePattern(term);
        if (pattern == null) {
            return null;
        }
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(GridQuerySupport.getOrCreateLeftJoin(root, "category").get("name")), pattern),
                cb.like(cb.lower(GridQuerySupport.getOrCreateLeftJoin(root, "client").get("name")), pattern));
    }
}
