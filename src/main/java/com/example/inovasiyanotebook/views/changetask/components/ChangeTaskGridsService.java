package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@UIScope
@Slf4j
public class ChangeTaskGridsService {
    private final CategoryGrid categoryGrid;
    private final ProductGridForNewChangeTask productGridForNewChangeTask;
    private final ChangeTaskItemsGrid changeTaskItemsGrid;

    private boolean viewMode = true;

    @PostConstruct
    public void init() {
        log.info("Initializing ChangeTaskGridsService...");
        initProductGrid();
        initCategoryGrid();
        configGridsVisible();
        log.info("ChangeTaskGridsService initialized successfully.");
    }

    public Set<Product> getSelectedProducts() {
        log.debug("Retrieving selected products in view mode: {}", viewMode);
        return viewMode ? null : productGridForNewChangeTask.getSelectedProducts();
    }

    public List<ChangeTaskItemDTO> getChangeTaskItemList() {
        log.debug("Retrieving change task items in view mode: {}", viewMode);
        return viewMode ? changeTaskItemsGrid.getChangeTaskItems() : null;
    }

    private void productSelected(Category category) {
        log.debug("Product selected: {}", category.getName());
        categoryGrid.selectCategory(category);
    }

    private void productDeselected(Category category) {
        log.debug("Product deselected: {}", category.getName());
        categoryGrid.unselectCategory(category);
    }

    private void categorySelected(Category category) {
        log.debug("Category selected: {}", category.getName());
        productGridForNewChangeTask.selectProducts(category);
    }

    private void categoryDeselected(Category category) {
        log.debug("Category deselected: {}", category.getName());
        productGridForNewChangeTask.deselectProducts(category);
    }

    public Component getCategoryGrid() {
        log.debug("Retrieving category grid component.");
        return categoryGrid.getCategoriesGrid();
    }

    public Component getProductGridForNewChangeTask() {
        log.debug("Retrieving product grid component.");
        return productGridForNewChangeTask.getProductGrid();
    }

    public Component getChangeTaskItemsGrid() {
        log.debug("Retrieving change task items grid component.");
        return changeTaskItemsGrid.getGrid();
    }

    public void setProducts(List<Product> products) {
        log.debug("Setting products and configuring grids visibility.");
        configGridsVisible();
        productGridForNewChangeTask.setProducts(products);
    }

    public void setChangeTaskItems(List<ChangeTaskItemDTO> changeTaskItems) {
        log.debug("Setting change task items and configuring grids visibility.");
        configGridsVisible();
        changeTaskItemsGrid.setChangeTask(changeTaskItems);
    }

    private void configGridsVisible() {
        log.debug("Configuring visibility for grids. View mode: {}", viewMode);
        changeTaskItemsGrid.setVisible(viewMode);
        productGridForNewChangeTask.setVisible(!viewMode);
        categoryGrid.setVisible(!viewMode);
    }

    public void clearSelectedProducts() {
        log.debug("Clearing selected products.");
        productGridForNewChangeTask.clearSelectedProducts();
    }

    private void initProductGrid() {
        log.debug("Initializing product grid.");
        productGridForNewChangeTask.setProductSelectedListener(this::productSelected);
        productGridForNewChangeTask.setProductDeselectedListener(this::productDeselected);
    }

    private void initCategoryGrid() {
        log.debug("Initializing category grid.");
        categoryGrid.setSelectedCategoryListener(this::categorySelected);
        categoryGrid.setDeselectedCategoryListener(this::categoryDeselected);
    }

    public void setViewMode(boolean viewMode) {
        log.debug("Setting view mode to: {}", viewMode);
        this.viewMode = viewMode;
        configGridsVisible();
    }
}
