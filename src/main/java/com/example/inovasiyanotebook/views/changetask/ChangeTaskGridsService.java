package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
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
    private final ProductGridForNewCangeTask productGridForNewCangeTask;
    private final ChangeTaskItemsGrid changeTaskItemsGrid;

    private boolean viewMode = false;

    @PostConstruct
    public void init() {
        initProductGrid();
        initCategoryGrid();
        configGridsVisible();

    }

    public Set<Product> getSelectedProducts() {
        return viewMode ? null : productGridForNewCangeTask.getSelectedProducts();
    }

    public List<ChangeTaskItem> getChangeTaskItemList(){
        return viewMode ? changeTaskItemsGrid.getChangeTaskItems() : null;
    }

    private void productSelected(Category category) {
        categoryGrid.selectCategory(category);
    }

    private void productDeselected(Category category) {
        categoryGrid.unselectCategory(category);
    }

    private void categorySelected(Category category) {
        productGridForNewCangeTask.selectProducts(category);
    }

    private void categoryDeselected(Category category) {
        productGridForNewCangeTask.deselectProducts(category);
    }

    public Component getCategoryGrid() {
        return categoryGrid.getCategoriesGrid();
    }

    public Component getProductGridForNewChangeTask() {
        return productGridForNewCangeTask.getProductGrid();
    }

    public Component getChangeTaskItemsGrid() {
        return changeTaskItemsGrid.getGrid();
    }

    public void setProducts(List<Product> products) {
        viewMode = false;
        configGridsVisible();

        productGridForNewCangeTask.setProducts(products);
    }

    public void setChangeTaskItems(List<ChangeTaskItem> changeTaskItems) {
        viewMode = true;
        configGridsVisible();

        changeTaskItemsGrid.setChangeTask(changeTaskItems);
    }

    private void configGridsVisible() {
        productGridForNewCangeTask.setVisible(!viewMode);
        categoryGrid.setVisible(!viewMode);
        changeTaskItemsGrid.setVisible(viewMode);
    }

    public void clearSelectedProducts() {
        productGridForNewCangeTask.clearSelectedProducts();
    }

    private void initProductGrid() {
        productGridForNewCangeTask.setProductSelectedListener(this::productSelected);
        productGridForNewCangeTask.setProductDeselectedListener(this::productDeselected);
    }

    private void initCategoryGrid() {
        categoryGrid.setSelectedCategoryListener(this::categorySelected);
        categoryGrid.setDeselectedCategoryListener(this::categoryDeselected);
    }
}
