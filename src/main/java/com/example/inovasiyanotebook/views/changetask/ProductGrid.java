package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class ProductGrid {
    private final ProductService productService;

    private Grid<Product> productGrid;
    private List<Product> products;
    @Setter
    private Consumer<Category> productSelectedListener;
    @Setter
    private Consumer<Category> productDeselectedListener;

    public void selectProducts(Category category) {
        var productsForSelect = productService.getAllByCategory(category);

        products.stream()
                .filter(product -> productsForSelect.stream()
                        .anyMatch(selected -> selected.getId().equals(product.getId())))
                .forEach(productGrid::select);
    }

    public void deselectProducts(Category category) {
        var productsForSelect = productService.getAllByCategory(category);

        products.stream()
                .filter(product -> productsForSelect.stream()
                        .anyMatch(selected -> selected.getId().equals(product.getId())))
                .forEach(productGrid::deselect);
    }

    public Component getProductGrid() {
        return productGrid;
    }

    @PostConstruct
    private void init() {
        // Создаем объект Grid для сущности Product
        productGrid = new Grid<>(Product.class);
        productGrid.setHeightFull(); // Устанавливаем полный размер по высоте
        products = productService.getAll();
        productGrid.setItems(products); // Устанавливаем список элементов
        productGrid.setSelectionMode(Grid.SelectionMode.MULTI); // Режим множественного выбора

        productGrid.removeAllColumns();
        productGrid.addColumn(Product::getName).setHeader("Mehsul adi"); //todo грамматика

        productGrid.addSelectionListener(event -> {
            if (event.isFromClient()) {
                var multiSelectionEvent = (MultiSelectionEvent) event;

                @SuppressWarnings("unchecked")
                Collection<Product> addedSelection = (Collection<Product>) multiSelectionEvent.getAddedSelection();

                addedSelection.stream()
                        .collect(Collectors.groupingBy(Product::getCategory))
                        .values().stream() // Преобразуем карту в поток записей (ключ-значение)
                        .map(products -> products.iterator().next()) // Берем первый продукт из каждой категории
                        .forEach(this::handleProductSelection);

                @SuppressWarnings("unchecked")
                Collection<Product> removedSelection = (Collection<Product>) multiSelectionEvent.getRemovedSelection();

                removedSelection.stream()
                        .map(Product::getCategory)
                        .collect(Collectors.toSet())
                        .forEach(productDeselectedListener);

            }
        });
    }

    private void handleProductSelection(Product selectedProduct) {
        Category productCategory = selectedProduct.getCategory();
        boolean allCategoryProductsSelected = productGrid.getSelectedItems().stream()
                .filter(product -> product.getCategory().equals(productCategory))
                .collect(Collectors.toSet())
                .size() == productService.getAllByCategory(productCategory).size();

        if (allCategoryProductsSelected && productSelectedListener != null) {
            productSelectedListener.accept(productCategory);
        }
    }

    public Set<Product> getSelectedProducts() {
        return productGrid.getSelectedItems();
    }

    public void setProducts(List<Product> productsForSelect) {
        products.stream()
                .filter(product -> productsForSelect.stream()
                        .anyMatch(selected -> selected.getId().equals(product.getId())))
                .forEach(productGrid::select);

        productGrid.getSelectedItems().stream()
                .collect(Collectors.groupingBy(Product::getCategory))
                .values().stream() // Преобразуем карту в поток записей (ключ-значение)
                .map(products -> products.iterator().next()) // Берем первый продукт из каждой категории
                .forEach(this::handleProductSelection);
    }

    public void clearSelectedProducts() {
        productGrid.getSelectedItems().clear();

        productGrid.getSelectedItems().stream()
                .collect(Collectors.groupingBy(Product::getCategory))
                .values().stream() // Преобразуем карту в поток записей (ключ-значение)
                .map(products -> products.iterator().next()) // Берем первый продукт из каждой категории
                .forEach(this::handleProductSelection);
    }
}
