package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@UIScope
public class ChangeTaskGridsService {
    private final CategoryGrid categoryGrid;
    private final ProductGridForNewChangeTask productGridForNewChangeTask;
    private final ChangeTaskItemsGrid changeTaskItemsGrid;


    private boolean viewMode = true;

    @PostConstruct
    public void init() {
        initProductGrid();
        initCategoryGrid();
        configGridsVisible();

    }

    public Set<Product> getSelectedProducts() {
        return viewMode ? null : productGridForNewChangeTask.getSelectedProducts();
    }

    public List<ChangeTaskItemDTO> getChangeTaskItemList() {
        return viewMode ? changeTaskItemsGrid.getChangeTaskItems() : null;
    }

    private void productSelected(Category category) {
        categoryGrid.selectCategory(category);
    }

    private void productDeselected(Category category) {
        categoryGrid.unselectCategory(category);
    }

    private void categorySelected(Category category) {
        productGridForNewChangeTask.selectProducts(category);
    }

    private void categoryDeselected(Category category) {
        productGridForNewChangeTask.deselectProducts(category);
    }

    public Component getCategoryGrid() {
        return categoryGrid.getCategoriesGrid();
    }

    public Component getProductGridForNewChangeTask() {
        return productGridForNewChangeTask.getProductGrid();
    }

    public Component getChangeTaskItemsGrid() {
        return changeTaskItemsGrid.getGrid();
    }

    public void setProducts(List<Product> products) {
        configGridsVisible();

        productGridForNewChangeTask.setProducts(products);
    }

    public void setChangeTaskItems(List<ChangeTaskItemDTO> changeTaskItems) {
        configGridsVisible();

        changeTaskItemsGrid.setChangeTask(changeTaskItems);
    }

    private void configGridsVisible() {
        changeTaskItemsGrid.setVisible(viewMode);
        productGridForNewChangeTask.setVisible(!viewMode);
        categoryGrid.setVisible(!viewMode);
    }

    public void clearSelectedProducts() {
        productGridForNewChangeTask.clearSelectedProducts();
    }

    private void initProductGrid() {
        productGridForNewChangeTask.setProductSelectedListener(this::productSelected);
        productGridForNewChangeTask.setProductDeselectedListener(this::productDeselected);
    }

    private void initCategoryGrid() {
        categoryGrid.setSelectedCategoryListener(this::categorySelected);
        categoryGrid.setDeselectedCategoryListener(this::categoryDeselected);
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;

        configGridsVisible();
    }
}
