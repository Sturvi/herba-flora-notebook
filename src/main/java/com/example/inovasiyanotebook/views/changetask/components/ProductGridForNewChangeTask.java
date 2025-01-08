package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class ProductGridForNewChangeTask {
    private final ProductService productService;
    private final DesignTools designTools;

    private Grid<Product> productGrid;
    private List<Product> products;
    private TextField searchField;
    private VerticalLayout gridLayout;
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
        return gridLayout;
    }

    @PostConstruct
    private void init() {
        productGrid = new Grid<>(Product.class);
        productGrid.setHeightFull();
        products = productService.getAllHerbaFloraProduct();
        updateGridItems(products);
        productGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        productGrid.removeAllColumns();
        productGrid.addColumn(Product::getName).setHeader("Mehsul adi"); //todo грамматика

        searchField = designTools.createTextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> filterGrid(event.getValue()));

        gridLayout = new VerticalLayout(searchField, productGrid);
        gridLayout.setClassName("full-size");
        gridLayout.addClassName("no-spacing");

        productGrid.addSelectionListener(event -> {
            if (event.isFromClient()) {
                var multiSelectionEvent = (MultiSelectionEvent) event;

                @SuppressWarnings("unchecked")
                Collection<Product> addedSelection = (Collection<Product>) multiSelectionEvent.getAddedSelection();

                addedSelection.stream()
                        .collect(Collectors.groupingBy(Product::getCategory))
                        .values().stream()
                        .map(products -> products.iterator().next())
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
                .size() == productService.getHerbaFloraProductsCountByCategory(productCategory);

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
        productGrid.deselectAll();

        productGrid.getSelectedItems().stream()
                .collect(Collectors.groupingBy(Product::getCategory))
                .values().stream() // Преобразуем карту в поток записей (ключ-значение)
                .map(products -> products.iterator().next()) // Берем первый продукт из каждой категории
                .forEach(this::handleProductSelection);
    }

    public void setVisible(boolean b) {
        productGrid.setVisible(b);
        searchField.setVisible(b);
        gridLayout.setVisible(b);
    }

    private void filterGrid(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            updateGridItems(products);
        } else {
            List<Product> filteredItems = products.stream()
                    .filter(item -> item.getName().toLowerCase().contains(filterText.toLowerCase()))
                    .collect(Collectors.toList());
            updateGridItems(filteredItems);
        }
    }

    private void updateGridItems(List<Product> items) {
        // Сортировка списка: сначала по статусу (ожидание выполнения), затем по другим критериям
        List<Product> sortedItems = items.stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());

        productGrid.setItems(new ListDataProvider<>(sortedItems));
    }
}
