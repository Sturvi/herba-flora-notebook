package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskItemService;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class ChangeTaskLayoutService {
    private final DesignTools designTools;
    private final ChangeTaskGridsService changeTaskGridsService;
    private final ChangeTaskService changeTaskService;
    private final ChangeTaskItemService changeTaskItemService;

    private TextField nameField;
    private TextArea descriptionField;
    @Getter
    private HorizontalLayout layout;
    private ChangeTask changeTask;

    public HorizontalLayout getNewLayout() {
        nameField.clear();
        descriptionField.clear();
        changeTaskGridsService.clearSelectedProducts();
        changeTask = new ChangeTask();

        return layout;
    }

    public void setChangeTaskData(ChangeTask changeTask) {
        this.changeTask = changeTask;

        nameField.setValue(changeTask.getTaskType());
        descriptionField.setValue(changeTask.getDescription());

        var selectedProducts = changeTask.getItems().stream()
                .map(ChangeTaskItem::getProduct)
                .toList();

        changeTaskGridsService.setProducts(selectedProducts);
    }


    private Button getButton(TextField nameField, TextArea descriptionField) {
        Button button = new Button("Yadda saxla");
        button.addClickListener(e -> {
            changeTask = changeTask == null ? new ChangeTask() : changeTask;
            changeTask.setTaskType(nameField.getValue());
            changeTask.setDescription(descriptionField.getValue());

            var selectedProducts = changeTaskGridsService.getSelectedProducts();

            var items = processChangeTaskItems(changeTask.getItems(), selectedProducts);

            changeTask.setItems(items);

            changeTaskService.create(changeTask);
        });
        return button;
    }

    private static ChangeTaskItem getNewChangeTaskItem(Product product, ChangeTask changeTask) {
        ChangeTaskItem ctItem = new ChangeTaskItem();
        ctItem.setProduct(product);
        ctItem.setTask(changeTask);
        ctItem.setStatus(ChangeItemStatus.PENDING);
        return ctItem;
    }

    public List<ChangeTaskItem> processChangeTaskItems(Collection<ChangeTaskItem> changeTaskItems, Collection<Product> selectedProducts) {
        // Сохраняем элементы, чьи продукты остаются в списке
        List<ChangeTaskItem> retainedItems = changeTaskItems.stream()
                .filter(item -> selectedProducts.stream()
                        .anyMatch(product -> product.getId().equals(item.getProduct().getId())))
                .toList();


        // Определяем новые продукты, которые отсутствуют в существующих элементах
        List<Product> newProducts = selectedProducts.stream()
                .filter(selectedProduct -> changeTaskItems.stream()
                        .noneMatch(item -> item.getProduct().getId().equals(selectedProduct.getId())))
                .toList();

        // Создаем новые ChangeTaskItem для новых продуктов
        List<ChangeTaskItem> newItems = newProducts.stream()
                .map(product -> getNewChangeTaskItem(product, changeTask))
                .toList();

        // Объединяем сохраненные элементы и новые элементы
        List<ChangeTaskItem> resultItems = new ArrayList<>(retainedItems);
        resultItems.addAll(newItems);

        return resultItems;
    }

    @PostConstruct
    public void init() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setPadding(false);


        VerticalLayout firstColumn = new VerticalLayout();
        VerticalLayout secondColumn = new VerticalLayout();
        secondColumn.setHeightFull();

        initFirstColum(firstColumn);
        secondColumn.add(changeTaskGridsService.getProductGrid());
        //initSecondColumn(secondColumn);

        layout.add(firstColumn, secondColumn);
    }


    private void initFirstColum(VerticalLayout firstColumn) {
        nameField = designTools.createTextField("Deyisiklik adi", null, null); //todo грамматика
        nameField.setWidthFull();
        descriptionField = designTools.createTextArea("Deyisiklik tesfiri", null, null);

        Button button = getButton(nameField, descriptionField);

        descriptionField.setClassName("change-task-text-area");

        var nameAndButtonLine = new HorizontalLayout(nameField, button);
        nameAndButtonLine.setWidthFull();
        nameAndButtonLine.setAlignItems(FlexComponent.Alignment.BASELINE);

        firstColumn.add(nameAndButtonLine, descriptionField, changeTaskGridsService.getCategoryGrid());
    }

}
