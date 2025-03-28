package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.dto.ProductOpenInfoDTO;
import com.example.inovasiyanotebook.model.client.Category;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO class for managing opened orders by product categories.
 * Класс DTO для управления открытыми заказами по категориям товаров.
 */
@Getter
@Slf4j
public class CategoryOpeningPositionDTO {

    private final Category category;
    private final List<ProductOpenInfoDTO> positionList;
    private LocalDate orderReceivedDate;

    /**
     * Constructor initializing category and default values.
     * Конструктор, инициализирующий категорию и значения по умолчанию.
     *
     * @param category the associated category / связанная категория.
     */
    public CategoryOpeningPositionDTO(Category category) {
        this.category = category;
        this.positionList = new ArrayList<>();
        this.orderReceivedDate = LocalDate.now();
        log.debug("Initialized CategoryOpeningPositionDTO for category: {}", category);
    }

    /**
     * Adds a product opening position to the list if it belongs to this category.
     * Updates the earliest order received date if necessary.
     * Добавляет позицию товара в список, если она принадлежит этой категории. Обновляет наранеешую дату заказа, если нужно.
     *
     * @param productOpeningPositionDTO the product position to add / добавляемая позиция товара.
     */
    public void addOpenedPosition(ProductOpenInfoDTO productOpeningPositionDTO) {
        if (productOpeningPositionDTO.getParentCategory().equals(category)) {
            positionList.add(productOpeningPositionDTO);
            log.debug("Added product position: {} to category: {}", productOpeningPositionDTO, category);
            if (productOpeningPositionDTO.getEarliestOrderReceivedDate().isBefore(this.orderReceivedDate)) {
                this.orderReceivedDate = productOpeningPositionDTO.getEarliestOrderReceivedDate();
                log.debug("Updated earliest order received date to: {}", this.orderReceivedDate);
            }
        } else {
            log.warn("Attempted to add product position with mismatched category: {}", productOpeningPositionDTO);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryOpeningPositionDTO that = (CategoryOpeningPositionDTO) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(category);
    }
}
