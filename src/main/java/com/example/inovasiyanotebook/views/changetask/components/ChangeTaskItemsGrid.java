package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
class ChangeTaskItemsGrid {
    private final ProductService productService;
    private final DesignTools designTools;

    private Grid<ChangeTaskItemDTO> changeTaskItemGrid;
    private TextField searchField;
    private List<ChangeTaskItemDTO> changeTaskItems;

    private VerticalLayout gridLayout;

    @PostConstruct
    public void init() {
        changeTaskItemGrid = new Grid<>();
        changeTaskItemGrid.setHeightFull();
        changeTaskItemGrid.setVisible(false);

        searchField = designTools.createTextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> filterGrid(event.getValue()));

        changeTaskItemGrid.addColumn(ChangeTaskItemDTO::getProductName).setHeader("Məhsul adı");
        changeTaskItemGrid.addColumn(item -> item.getCompletedAt() == null ? null : item.getCompletedAt().toLocalDate()).setHeader("Bitmə tarixi");
        changeTaskItemGrid.addComponentColumn(item -> {
            var comboBox = designTools.creatComboBox(null, Arrays.stream(ChangeItemStatus.values()).toList(), ChangeItemStatus::getDescription, item.getStatus());
            comboBox.addValueChangeListener(attachEvent -> {
                item.setStatus(comboBox.getValue());
            });
            return comboBox;
        });

        gridLayout = new VerticalLayout(searchField, changeTaskItemGrid);
        gridLayout.setClassName("full-size");
        gridLayout.addClassName("no-spacing");
    }

    public void setChangeTask(List<ChangeTaskItemDTO> items) {
        this.changeTaskItems = items;
        updateGridItems(items);
        changeTaskItemGrid.setVisible(true);
    }

    public List<ChangeTaskItemDTO> getChangeTaskItems() {
        return changeTaskItems;
    }

    public Component getGrid() {
        return gridLayout;
    }

    public void setVisible(boolean visible) {
        gridLayout.setVisible(visible);
        changeTaskItemGrid.setVisible(visible);
        searchField.setVisible(visible);
    }

    private void filterGrid(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            updateGridItems(changeTaskItems);
        } else {
            List<ChangeTaskItemDTO> filteredItems = changeTaskItems.stream()
                    .filter(item -> item.getProductName().toLowerCase().contains(filterText.toLowerCase()))
                    .collect(Collectors.toList());
            updateGridItems(filteredItems);
        }
    }

    private void updateGridItems(List<ChangeTaskItemDTO> items) {
        // Сортировка списка: сначала по статусу (ожидание выполнения), затем по другим критериям
        List<ChangeTaskItemDTO> sortedItems = items.stream()
                .sorted((item1, item2) -> {
                    if (item1.getStatus() == ChangeItemStatus.PENDING && item2.getStatus() != ChangeItemStatus.PENDING) {
                        return -1; // "Ожидание выполнения" выше
                    } else if (item1.getStatus() != ChangeItemStatus.PENDING && item2.getStatus() == ChangeItemStatus.PENDING) {
                        return 1; // Другие статусы ниже
                    }
                    return item1.getProductName().compareToIgnoreCase(item2.getProductName());
                })
                .collect(Collectors.toList());

        changeTaskItemGrid.setItems(new ListDataProvider<>(sortedItems));
    }

}
