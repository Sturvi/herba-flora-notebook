package com.example.inovasiyanotebook.service.viewservices.product.extrainformationdialog;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.service.entityservices.ProductExtraInfoService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductExtraInfoDialogService {

    private final ProductService productService;
    private final ProductExtraInfoService productExtraInfoService;

    public void openDialog(Product product) {
        // Create a fresh list of extra info to avoid side effects
        List<ProductExtraInfo> temporaryExtraInfo = productExtraInfoService.getAllByProductId(product.getId());

        Dialog dialog = createDialog(product, temporaryExtraInfo);

        VerticalLayout contentLayout = createContentLayout(product, temporaryExtraInfo);

        dialog.add(contentLayout);
        dialog.open();
    }

    private Dialog createDialog(Product product, List<ProductExtraInfo> temporaryExtraInfo) {
        Dialog dialog = new Dialog();
        dialog.setHeightFull();
        dialog.setWidthFull();
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        HorizontalLayout headerLayout = createHeaderLayout(product, temporaryExtraInfo, dialog);
        dialog.getHeader().add(headerLayout);

        return dialog;
    }

    private HorizontalLayout createHeaderLayout(Product product, List<ProductExtraInfo> temporaryExtraInfo, Dialog dialog) {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button saveButton = new Button("Save", event -> {
            productService.updateExtraInfo(product, temporaryExtraInfo);
            dialog.close();
        });

        Button closeButton = new Button("Close", event -> dialog.close());

        headerLayout.add(saveButton, closeButton);
        return headerLayout;
    }

    private VerticalLayout createContentLayout(Product product, List<ProductExtraInfo> temporaryExtraInfo) {
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout inputLayout = createNewInfoInputRow(product, temporaryExtraInfo, layout);
        layout.add(inputLayout);

        temporaryExtraInfo.forEach(extraInfo -> layout.add(createExtraInfoRow(extraInfo, temporaryExtraInfo, layout)));

        return layout;
    }

    private HorizontalLayout createNewInfoInputRow(Product product, List<ProductExtraInfo> temporaryExtraInfo, VerticalLayout parentLayout) {
        TextArea keyField = new TextArea("Key");
        TextArea valueField = new TextArea("Value");
        keyField.setWidthFull();
        valueField.setWidthFull();

        Button addButton = new Button("Add", event -> {
            String key = keyField.getValue();
            String value = valueField.getValue();

            if (!key.isEmpty() && !value.isEmpty()) {
                ProductExtraInfo newInfo = new ProductExtraInfo();
                newInfo.setKey(key);
                newInfo.setValue(value);
                newInfo.setProduct(product);

                temporaryExtraInfo.add(newInfo);
                parentLayout.add(createExtraInfoRow(newInfo, temporaryExtraInfo, parentLayout));

                keyField.clear();
                valueField.clear();
            }
        });

        HorizontalLayout inputLayout = new HorizontalLayout(keyField, valueField, addButton);
        inputLayout.setWidthFull();
        inputLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        return inputLayout;
    }

    private HorizontalLayout createExtraInfoRow(ProductExtraInfo extraInfo, List<ProductExtraInfo> temporaryExtraInfo, VerticalLayout parentLayout) {
        TextArea keyField = new TextArea();
        keyField.setValue(extraInfo.getKey());
        keyField.setWidth("40%");
        keyField.setReadOnly(true);

        TextArea valueField = new TextArea();
        valueField.setValue(extraInfo.getValue());
        valueField.setWidth("60%");
        valueField.setReadOnly(true);

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        HorizontalLayout rowLayout = new HorizontalLayout(keyField, valueField, editButton, deleteButton);
        rowLayout.setWidthFull();
        rowLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        editButton.addClickListener(event -> {
            boolean isEditing = !keyField.isReadOnly();
            if (isEditing) {
                extraInfo.setKey(keyField.getValue());
                extraInfo.setValue(valueField.getValue());
                keyField.setReadOnly(true);
                valueField.setReadOnly(true);
                editButton.setText("Edit");
            } else {
                keyField.setReadOnly(false);
                valueField.setReadOnly(false);
                editButton.setText("Remember");
            }
        });

        deleteButton.addClickListener(event -> {
            temporaryExtraInfo.remove(extraInfo);
            parentLayout.remove(rowLayout);
        });

        return rowLayout;
    }
}
