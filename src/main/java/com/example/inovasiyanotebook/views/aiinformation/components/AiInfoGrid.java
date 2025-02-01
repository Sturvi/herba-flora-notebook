package com.example.inovasiyanotebook.views.aiinformation.components;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
@Slf4j
public class AiInfoGrid {
    private Grid<Product> grid;
    private ListDataProvider<Product> dataProvider;

    private final ProductService productService;

    @PostConstruct
    public void init() {
        grid = new Grid<>();
        grid.setHeightFull();
        grid.setWidthFull();

        var products = productService.getAllHerbaFloraProduct().stream().sorted(Comparator.comparing(Product::getName)).toList();
        dataProvider = new ListDataProvider<>(products);
        grid.setItems(dataProvider);

        grid.addColumn(Product::getName).setHeader("Mehsul adi");

    }

    public Component getGrid() {
        return grid;
    }

    public void filterGrid(String filterText) {
        log.debug("Applying filter to grid. Filter text: {}", filterText);
        dataProvider.clearFilters();

        if (filterText != null && !filterText.isEmpty()) {
            dataProvider.addFilter(product -> product.getName().toLowerCase().contains(filterText.toLowerCase()));
        }
    }
}
