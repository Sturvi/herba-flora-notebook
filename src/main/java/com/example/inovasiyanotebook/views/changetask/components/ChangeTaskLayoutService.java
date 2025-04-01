package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskItemDTO;
import com.example.inovasiyanotebook.mapper.ChangeTaskItemMapper;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskItemService;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@UIScope
@Slf4j
public class ChangeTaskLayoutService {
    private final DesignTools designTools;
    private final ChangeTaskGridsService changeTaskGridsService;
    private final ChangeTaskService changeTaskService;
    private final ChangeTaskItemService changeTaskItemService;
    private final NavigationTools navigationTools;
    private final ChangeTaskItemMapper mapper;

    private TextField nameField;
    private TextArea descriptionField;
    private Button editButton;
    private Button deleteButton;
    private HorizontalLayout layout;
    private ChangeTask changeTask;
    private boolean viewMode;

    public HorizontalLayout getNewLayout() {
        log.debug("Initializing new layout for ChangeTask.");
        viewMode = false;
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        nameField.clear();
        descriptionField.clear();
        changeTaskGridsService.clearSelectedProducts();
        changeTaskGridsService.setViewMode(viewMode);
        changeTask = new ChangeTask();

        return layout;
    }

    public Component getLayout() {
        log.debug("Returning layout component.");
        return layout;
    }

    public void setChangeTaskData(ChangeTask changeTask, boolean viewMode) {
        log.debug("Setting data for ChangeTask. Task ID: {}, View Mode: {}", changeTask.getId(), viewMode);
        this.changeTask = changeTask;
        this.viewMode = viewMode;
        editButton.setVisible(viewMode);
        deleteButton.setVisible(viewMode);

        nameField.setValue(changeTask.getTaskType());
        descriptionField.setValue(changeTask.getDescription());

        nameField.setReadOnly(viewMode);
        descriptionField.setReadOnly(viewMode);

        changeTaskGridsService.setChangeTaskItems(changeTask.getItems().stream().map(mapper::toDTO).toList());
        var selectedProducts = changeTask.getItems().stream()
                .map(ChangeTaskItem::getProduct)
                .toList();

        changeTaskGridsService.setProducts(selectedProducts);
        changeTaskGridsService.setViewMode(viewMode);
    }

    private static ChangeTaskItem getNewChangeTaskItem(Product product, ChangeTask changeTask) {
        ChangeTaskItem ctItem = new ChangeTaskItem();
        ctItem.setProduct(product);
        ctItem.setTask(changeTask);
        ctItem.setStatus(ChangeItemStatus.PENDING);
        return ctItem;
    }

    public List<ChangeTaskItem> processChangeTaskItems(Collection<ChangeTaskItem> changeTaskItems, Collection<Product> selectedProducts) {
        log.debug("Processing ChangeTaskItems. Selected products count: {}", selectedProducts.size());

        List<ChangeTaskItem> retainedItems = changeTaskItems.stream()
                .filter(item -> selectedProducts.stream()
                        .anyMatch(product -> product.getId().equals(item.getProduct().getId())))
                .toList();

        List<Product> newProducts = selectedProducts.stream()
                .filter(selectedProduct -> changeTaskItems.stream()
                        .noneMatch(item -> item.getProduct().getId().equals(selectedProduct.getId())))
                .toList();

        List<ChangeTaskItem> newItems = newProducts.stream()
                .map(product -> getNewChangeTaskItem(product, changeTask))
                .toList();

        List<ChangeTaskItem> resultItems = new ArrayList<>(retainedItems);
        resultItems.addAll(newItems);

        log.debug("Processed ChangeTaskItems. Total items: {}", resultItems.size());
        return resultItems;
    }

    @PostConstruct
    private void init() {
        log.info("Initializing ChangeTaskLayoutService...");
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setPadding(false);

        VerticalLayout firstColumn = new VerticalLayout();
        VerticalLayout secondColumn = new VerticalLayout();
        secondColumn.setHeightFull();

        initFirstColumn(firstColumn);
        secondColumn.add(changeTaskGridsService.getChangeTaskItemsGrid(), changeTaskGridsService.getProductGridForNewChangeTask());

        layout.add(firstColumn, secondColumn);
        log.info("ChangeTaskLayoutService initialized successfully.");
    }

    private void initFirstColumn(VerticalLayout firstColumn) {
        log.debug("Initializing first column.");
        nameField = designTools.createTextField("Dəyişiklik adı\n", null, null);
        nameField.setWidthFull();
        descriptionField = designTools.createTextArea("Dəyişiklik təsviri", null, null);

        Button saveButton = getSaveButton(nameField, descriptionField);
        editButton = getEditButton();
        deleteButton = getDeleteButton();

        descriptionField.setClassName("change-task-text-area");

        var nameAndButtonLine = new HorizontalLayout(nameField, deleteButton, editButton, saveButton);
        nameAndButtonLine.setWidthFull();
        nameAndButtonLine.setAlignItems(FlexComponent.Alignment.BASELINE);

        firstColumn.add(nameAndButtonLine, descriptionField, changeTaskGridsService.getCategoryGrid());
    }

    private Button getDeleteButton() {
        Button button = new Button("Dəyişikliyi sil");
        button.addClickListener(e -> {
            changeTaskService.delete(changeTask);
            Notification.show("Dəyişiklik silindi", 5000, Notification.Position.MIDDLE);
            navigationTools.navigateTo(ViewsEnum.CHANGE_TASK);
        });

        return button;
    }

    private Button getEditButton() {
        log.debug("Creating edit button.");
        Button button = new Button("Dəyişdir");

        button.addClickListener(e -> {
            log.debug("Edit button clicked.");
            var newChangeTask = changeTaskService.getByIdWithItems(changeTask.getId());
            setChangeTaskData(newChangeTask.get(), false);
            button.setVisible(viewMode);
        });

        return button;
    }

    private Button getSaveButton(TextField nameField, TextArea descriptionField) {
        log.debug("Creating save button.");
        Button button = new Button("Yadda saxla");
        button.addClickListener(e -> {
            log.debug("Save button clicked. View mode: {}", viewMode);
            if (viewMode) {
                var newStatusesMap = changeTaskGridsService.getChangeTaskItemList()
                        .stream()
                        .collect(Collectors.toMap(ChangeTaskItemDTO::getId, ChangeTaskItemDTO::getStatus));
                changeTask.getItems()
                        .forEach(item -> {
                            item.setStatus(newStatusesMap.get(item.getId()));
                            changeTaskItemService.update(item);
                        });
                Notification.show("Məlumat yeniləndi", 5000, Notification.Position.MIDDLE);
            } else {
                changeTask = changeTask == null ? new ChangeTask() : changeTask;
                changeTask.setTaskType(nameField.getValue());
                changeTask.setDescription(descriptionField.getValue());

                var selectedProducts = changeTaskGridsService.getSelectedProducts();
                var items = processChangeTaskItems(changeTask.getItems(), selectedProducts);

                changeTask.setItems(items);

                changeTaskService.create(changeTask);

                Notification.show(changeTask.getId() == null ? "Dəyişiklik yaradıldı" : "Dəyişiklik yeniləndi", 5000, Notification.Position.MIDDLE);

                viewMode = true;
                changeTaskGridsService.setViewMode(true);

                navigationTools.navigateTo(ViewsEnum.CHANGE_TASK);
            }
        });
        return button;
    }
}
