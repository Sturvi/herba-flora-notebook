package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.client.Category;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
public class ChangeTaskGridsService {
    private final CategoryGrid categoryGrid;
    private final ProductGrid productGrid;



    @PostConstruct
    public void init() {
        initProductGrid();
        initCategoryGrid();

    }

    private void productSelected(Category category) {
        categoryGrid.selectCategory(category);
    }

    private void productDeselected(Category category) {
        categoryGrid.unselectCategory(category);
    }

    private void categorySelected(Category category) {
        productGrid.selectProducts(category);
    }

    private void categoryDeselected(Category category) {
        productGrid.deselectProducts(category);
    }

    public Component getCategoryGrid() {
        return categoryGrid.getCategoriesGrid();
    }

    public Component getProductGrid() {
        return productGrid.getProductGrid();
    }

    private void initProductGrid() {
        productGrid.setProductSelectedListener(this::productSelected);
        productGrid.setProductDeselectedListener(this::productDeselected);
    }

    private void initCategoryGrid() {
        categoryGrid.setSelectedCategoryListener(this::categorySelected);
        categoryGrid.setDeselectedCategoryListener(this::categoryDeselected);
    }
}
