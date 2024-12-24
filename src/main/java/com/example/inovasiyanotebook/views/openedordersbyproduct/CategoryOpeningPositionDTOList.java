package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.client.Category;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryOpeningPositionDTOList {
    private Map<Category, CategoryOpeningPositionDTO> categoryCategoryOpeningPositionDTOMap;

    public CategoryOpeningPositionDTOList() {
        this.categoryCategoryOpeningPositionDTOMap = new HashMap<>();
    }

    public void addProductOpeningPositionDTO(ProductOpeningPositionDTO productOpeningPositionDTO) {
        if (categoryCategoryOpeningPositionDTOMap.containsKey(productOpeningPositionDTO.getParentCategory())) {
            categoryCategoryOpeningPositionDTOMap.get(productOpeningPositionDTO.getParentCategory()).addOpenedPosition(productOpeningPositionDTO);
        } else {
            CategoryOpeningPositionDTO categoryOpeningPositionDTO = new CategoryOpeningPositionDTO(productOpeningPositionDTO.getParentCategory());
            categoryOpeningPositionDTO.addOpenedPosition(productOpeningPositionDTO);
            categoryCategoryOpeningPositionDTOMap.put(productOpeningPositionDTO.getParentCategory(), categoryOpeningPositionDTO);
        }
    }

    public List<CategoryOpeningPositionDTO> getCategoryOpeningPositionDTOList() {
        return categoryCategoryOpeningPositionDTOMap.values().stream()
                .sorted(Comparator.comparing(CategoryOpeningPositionDTO::getOrderReceivedDate))
                .collect(Collectors.toList());
    }
}
