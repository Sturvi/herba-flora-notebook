package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.PrintedTypeService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
@Slf4j
public class PrintedTypeGrid {
    private final DesignTools designTools;
    private final PermissionsCheck permissionsCheck;
    private final PrintedTypeService printedTypeService;
    private final NavigationTools navigationTools;

    private Grid<PrintedType> printedTypeGrid;
    private GridListDataView<PrintedType> dataList;

    public void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeightFull();
        dialog.setMinWidth("500px");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button closeButton = designTools.getNewIconButton(new Icon("lumo", "cross"), dialog::close);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Создаем заголовок для диалога
        var header = createHeaderLine();

        FlexLayout headerLayout = new FlexLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.add(header, closeButton); // Добавляем заголовок и кнопку закрытия

        dialog.getHeader().add(headerLayout);

        var printedTypes = printedTypeService.getAll();

        printedTypeGrid = new Grid<>();
        printedTypeGrid.setHeightFull();
        printedTypeGrid.addColumn(PrintedType::getName)
                .setHeader("Adı")
                .setSortable(true)
                .setFlexGrow(5);


        if (permissionsCheck.needEditor()) {
            var editAndDeleteButtons = printedTypeGrid.addComponentColumn(printedType -> {
                Button editButton = designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> editDialog(printedType));
                Button deleteButton = designTools.getNewIconButton(VaadinIcon.TRASH.create(), () -> deleteHandler(printedType));

                return new HorizontalLayout(editButton, deleteButton);
            });

            editAndDeleteButtons.setWidth("75px");
            editAndDeleteButtons.setFlexGrow(1);
        }

        GridListDataView<PrintedType> dataList = printedTypeGrid.setItems(printedTypes);

        var verticalLayout = new VerticalLayout(printedTypeGrid);
        verticalLayout.setHeightFull();
        dialog.add(verticalLayout);
        dialog.open();
    }

    private void refreshData() {
        var printedTypes = printedTypeService.getAll();
        printedTypeGrid.setItems(printedTypes);
    }

    private void deleteHandler(PrintedType printedType) {
        designTools.showConfirmationDialog(() -> {
            printedTypeService.delete(printedType);
            refreshData();
        });

    }

    private void editDialog(PrintedType printedType) {
        Dialog dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setMaxWidth("600px");

        TextField textField = designTools.createTextField("Çap növü", "^.*$", "");
        textField.setValue(printedType.getName());

        Button addButton = getButton(printedType, textField, dialog);

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> dialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(addButton, cancelButton);
        buttonsLayout.addClassName("small-button");

        VerticalLayout verticalLayout = new VerticalLayout(textField, buttonsLayout);
        verticalLayout.setHeightFull();

        dialog.add(verticalLayout);

        dialog.open();
    }

    private Button getButton(PrintedType printedType, TextField textField, Dialog dialog) {
        Button addButton = new Button("Yenilə");
        addButton.addClickListener(buttonClickEvent -> {
            if (!textField.getValue().isEmpty()) {
                try {
                    printedType.setName(textField.getValue());
                    printedTypeService.update(printedType);
                    dialog.close();
                    refreshData();
                } catch (Exception e) {
                    textField.setErrorMessage("Bu adnan çap növü artıq mövcutdur");
                    textField.setInvalid(true);
                }
            } else {
                textField.setErrorMessage("Boş ola bilməz");
                textField.setInvalid(true);
            }
        });
        return addButton;
    }

    private HorizontalLayout createHeaderLine() {
        var header = new H4("Çap növləri");
        header.setWidthFull();
        HorizontalLayout horizontalLayout = new HorizontalLayout(header);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        if (permissionsCheck.needEditor()) {
            var newPrintedTypeNameField = designTools.createTextField("", "^.*$", "");
            newPrintedTypeNameField.setVisible(false);
            newPrintedTypeNameField.setWidthFull();
            var addButton = designTools.getNewIconButton(VaadinIcon.PLUS.create(), () -> {
            });
            addButton.addClickListener(event -> {
                changeVisible(addButton, newPrintedTypeNameField);
                newPrintedTypeNameField.focus();
            });

            newPrintedTypeNameField.addKeyDownListener(Key.ENTER, keyDownEvent -> {
                addNewPrintedType(newPrintedTypeNameField);
                changeVisible(newPrintedTypeNameField, addButton);
            });
            newPrintedTypeNameField.addBlurListener(textFieldBlurEvent -> {
                addNewPrintedType(newPrintedTypeNameField);
                changeVisible(newPrintedTypeNameField, addButton);
            });
            newPrintedTypeNameField.addKeyDownListener(Key.ESCAPE, keyDownEvent -> changeVisible(newPrintedTypeNameField, addButton));

            horizontalLayout.add(newPrintedTypeNameField, addButton);
        }

        return horizontalLayout;
    }

    private void addNewPrintedType(TextField textField) {
        if (!textField.getValue().isEmpty()) {
            try {
                var printedType = PrintedType.builder().name(textField.getValue()).build();
                printedTypeService.create(printedType);
                refreshData();
            } catch (Exception e) {
                log.error("Error addin new PrintedType. Dublicate value");
            }
        }
    }

    private void changeVisible(Component invisibleComponent, Component visibleComponent) {
        invisibleComponent.setVisible(false);
        visibleComponent.setVisible(true);
    }
}
