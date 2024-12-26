package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * DTO class to manage the relationship between a product and its open order positions.
 * Класс DTO для управления связью между продуктом и его открытыми позициями заказов.
 */
@Getter
@Slf4j
public class ProductOpeningPositionDTO {

    private final Product product;
    private final TreeSet<OrderPosition> openOrderPositions;
    private LocalDate earliestOrderReceivedDate;
    private final Category parentCategory;

    /**
     * Constructor initializes the DTO with a product, calculating its top-level parent category.
     * @param product the product to initialize the DTO with
     * @throws IllegalArgumentException if the product is null
     *
     * Конструктор инициализирует DTO списком продуктов и рассчитывает их главную категорию.
     */
    public ProductOpeningPositionDTO(Product product) {
        if (product == null) {
            log.error("Product cannot be null");
            throw new IllegalArgumentException("Product cannot be null");
        }

        this.product = product;

        Category topCategory = product.getCategory();
        while (topCategory.hasParent()) {
            topCategory = topCategory.getParentCategory();
        }
        this.parentCategory = topCategory;

        this.openOrderPositions = new TreeSet<>(Comparator.comparing(op -> op.getOrder().getOrderReceivedDate()));
        this.earliestOrderReceivedDate = LocalDate.now();
    }

    /**
     * Adds an order position to the openOrderPositions set if its product matches the DTO's product.
     * Updates the earliest order received date if the new position's order date is earlier.
     * @param orderPosition the order position to add
     *
     * Добавляет позицию заказа в набор openOrderPositions, если продукт совпадает.
     * Обновляет самую раннюю дату получения заказа, если она ранее.
     */
    public void addOrderPositionIfMatchesProduct(OrderPosition orderPosition) {
        if (orderPosition.getProduct().equals(product)) {
            this.openOrderPositions.add(orderPosition);

            if (orderPosition.getOrder().getOrderReceivedDate().isBefore(this.earliestOrderReceivedDate)) {
                this.earliestOrderReceivedDate = orderPosition.getOrder().getOrderReceivedDate();
            }
            log.info("OrderPosition added successfully: {}", orderPosition);
        } else {
            log.warn("OrderPosition does not match the product and will not be added: {}", orderPosition);
        }
    }

    /**
     * Retrieves a list of open order positions.
     * @return list of open order positions
     *
     * Возвращает список открытых позиций заказов.
     */
    public List<OrderPosition> getOpenOrderPositions() {
        return openOrderPositions.stream().toList();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductOpeningPositionDTO that = (ProductOpeningPositionDTO) o;
        return Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(product);
    }
}
