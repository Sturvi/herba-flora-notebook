package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.dto.ProductOpenInfoDTO;
import com.example.inovasiyanotebook.model.client.Category;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CategoryOpeningPositionDTOList {
    // Map to store categories and their respective positions
    // Карта для хранения категорий и соответствующих им позиций
    private final Map<Category, CategoryOpeningPositionDTO> categoryToPositionMap;

    public CategoryOpeningPositionDTOList() {
        this.categoryToPositionMap = new HashMap<>();
        log.debug("Initialized CategoryOpeningPositionDTOList with an empty map."); // Log initialization
    }

    /**
     * Adds a product position to the corresponding category. If the category doesn't exist in the map,
     * it creates a new entry.
     * Добавляет позицию продукта в соответствующую категорию. Если категория отсутствует в карте,
     * создается новая запись.
     *
     * @param productPosition ProductOpeningPositionDTO to add.
     *                       Позиция продукта для добавления.
     */
    public void addProductPosition(ProductOpenInfoDTO productPosition) {
        log.debug("Adding product position: {}", productPosition);
        Category parentCategory = productPosition.getParentCategory();
        CategoryOpeningPositionDTO categoryPosition = categoryToPositionMap.computeIfAbsent(
                parentCategory,
                key -> {
                    log.debug("Creating new CategoryOpeningPositionDTO for category: {}", key);
                    // Создание нового CategoryOpeningPositionDTO для категории: {}
                    return new CategoryOpeningPositionDTO(parentCategory);
                }
        );
        categoryPosition.addOpenedPosition(productPosition);
        log.info("Product position added to category: {}", parentCategory);
    }

    /**
     * Retrieves a sorted list of CategoryOpeningPositionDTO objects based on their order received dates.
     * Возвращает отсортированный список объектов CategoryOpeningPositionDTO по дате получения заказа.
     *
     * @return Sorted list of CategoryOpeningPositionDTO.
     *         Отсортированный список CategoryOpeningPositionDTO.
     */
    public List<CategoryOpeningPositionDTO> getSortedCategoryPositions() {
        log.debug("Sorting CategoryOpeningPositionDTO list by order received date.");
        List<CategoryOpeningPositionDTO> sortedList = categoryToPositionMap.values().stream()
                .sorted(Comparator.comparing(CategoryOpeningPositionDTO::getOrderReceivedDate))
                .collect(Collectors.toList());
        log.info("Retrieved sorted list of CategoryOpeningPositionDTO objects.");
        return sortedList;
    }
}
