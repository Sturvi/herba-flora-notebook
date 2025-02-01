package com.example.inovasiyanotebook.views;

import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEventPublisher;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;


@Service
@UIScope
@RequiredArgsConstructor
public class DesignTools {
    private final PermissionsCheck permissionsCheck;
    private final NavigationTools navigationTools;
    private final ClientListUpdateCommandEventPublisher clientListUpdateCommandEventPublisher;


    public HorizontalLayout addEmptySpace() {
        HorizontalLayout space = new HorizontalLayout();
        space.setWidthFull();
        space.setHeight("20px");

        return space;
    }

    public Button getAddButton(Runnable clickHandleFunction) {
        return getNewIconButton(new Icon(VaadinIcon.PLUS), clickHandleFunction);
    }

    public Button getTrashButton(Runnable clickHandleFunction) {
        return getNewIconButton(new Icon(VaadinIcon.TRASH), clickHandleFunction);
    }

    public Button getNewIconButton(Icon icon, Runnable clickHandleFunction) {
        Button button = new Button(icon);
        button.addClickListener(e -> clickHandleFunction.run());
        button.setClassName("small-button");

        return button;
    }

    public void creatDialog(Dialog dialog, List<Component> desktopComponents, List<Component> mobileComponents) {
        var desktopView = getDesktopView(desktopComponents);
        desktopView.setWidthFull();
        var mobileView = getMobileView(mobileComponents);
        mobileView.setWidthFull();

        dialog.setWidth("75%");

        addResponsive(desktopView, mobileView);
        dialog.add(desktopView, mobileView);

        dialog.open();
    }

    public void showConfirmationDialog(Runnable deleteAction) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setMaxWidth("400px");

        H2 sureText = new H2("Əminmisiniz?");

        Button yesButton = new Button("Bəli");
        yesButton.addClickListener(click -> {
            deleteAction.run();
            confirmationDialog.close();
        });

        Button noButton = new Button("Xeyr");
        noButton.addClickListener(click -> confirmationDialog.close());

        HorizontalLayout horizontalLayout = new HorizontalLayout(yesButton, noButton);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout(sureText, horizontalLayout);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        confirmationDialog.add(verticalLayout);
        confirmationDialog.open();
    }

    private VerticalLayout getDesktopView(List<Component> components) {
        VerticalLayout desktopView = new VerticalLayout();
        HorizontalLayout currentRow = new HorizontalLayout(); // Строка для размещения компонентов
        currentRow.setWidthFull();
        int componentCount = 0; // Счетчик компонентов в текущей строке

        for (Component component : components) {
            currentRow.add(component);
            componentCount++;

            // Если в строке уже 2 компонента, добавляем строку в desktopView и создаем новую
            if (componentCount == 2) {
                desktopView.add(currentRow);
                currentRow = new HorizontalLayout();
                currentRow.setWidthFull();
                currentRow.setAlignItems(FlexComponent.Alignment.BASELINE);
                componentCount = 0;
            }
        }

        // Добавляем последнюю строку, если в ней есть компоненты
        if (componentCount > 0) {
            desktopView.add(currentRow);
        }

        desktopView.setWidthFull();
        desktopView.setVisible(false);

        return desktopView;
    }


    private VerticalLayout getMobileView(List<Component> components) {
        VerticalLayout mobileView = new VerticalLayout();
        mobileView.add(components);

        mobileView.setWidthFull();
        mobileView.setVisible(false);

        return mobileView;
    }

    public TextField createTextField(String label, String pattern, String errorMessage) {
        TextField textField = new TextField();
        textField.setLabel(label);
        textField.setWidthFull();
        if (pattern != null) {
            textField.setPattern(pattern);
            textField.setErrorMessage(errorMessage);
        }
        return textField;
    }

    public TextField createTextField() {
        TextField textField = new TextField();
        textField.setWidthFull();
        return textField;
    }

    public TextField createTextFieldWithValue(String label, String value, boolean readOnly) {
        TextField textField = new TextField();
        textField.setLabel(label);
        textField.setValue(value);
        textField.setReadOnly(readOnly);
        textField.setWidthFull();

        return textField;
    }

    public TextArea createTextArea(String label, String pattern, String errorMessage) {
        return createTextArea(label, pattern, errorMessage, null);
    }

    public TextArea createTextArea(String label, String pattern, String errorMessage, String value) {
        TextArea textArea = new TextArea();
        textArea.setLabel(label);
        textArea.addClassName("text-area");
        if (pattern != null) {
            textArea.setPattern(pattern);
            textArea.setErrorMessage(errorMessage);
        }

        if (value != null) {
            textArea.setValue(value);
        }

        return textArea;
    }

    public void addEditableField(NamedEntity client,
                                 VerticalLayout layout,
                                 String titleText,
                                 String value,
                                 String regex,
                                 String errorMessage,
                                 BiConsumer<NamedEntity, String> updateFunction) {
        H4 title = new H4(titleText);
        H4 displayValue = getInformationH4(value);
        TextField editField = createEditableField(client, displayValue, regex, errorMessage, updateFunction);

        HorizontalLayout line = new HorizontalLayout(title, displayValue, editField);
        line.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER); // Центрирование элементов по вертикали
        line.setFlexGrow(1, title); // Растягивание заголовка для выравнивания остальных элементов
        layout.add(line);
    }

    public <T extends NamedEntity> void processEntityUpdate(T entity,
                                                            TextField textField,
                                                            HtmlContainer htmlContainer,
                                                            BiConsumer<T, String> updateFunction) {
        String newText = textField.getValue().trim();

        if (newText.matches(textField.getPattern())) {
            if (newText.isEmpty()) {
                setEmptyFieldStyle(htmlContainer, "Əlavə et");
            } else {
                resetFieldStyle(htmlContainer, newText);
            }
            updateFunction.accept(entity, newText);
        } else {
            textField.setVisible(true);
            textField.focus();
            return;
        }

        textField.setVisible(false);
        htmlContainer.setVisible(true);
    }

    public <T> ComboBox<T> creatComboBox(String boxName, List<T> dataList, Function<T, String> nameFunction, T value) {
        ComboBox<T> comboBox = new ComboBox<>(boxName);
        comboBox.setItems(dataList);
        comboBox.setItemLabelGenerator(nameFunction::apply);
        comboBox.setErrorMessage("Boş ola bilməz");
        comboBox.setValue(value);
        comboBox.setWidthFull();

        return comboBox;
    }

    public <T> ComboBox<T> creatComboBox(String boxName, List<T> dataList, Function<T, String> nameFunction) {
        return creatComboBox(boxName, dataList, nameFunction, null);
    }

    public void addResponsive(Component desktopView, Component mobileView) {
        Page page = UI.getCurrent().getPage();

        // Устанавливаем начальную видимость макетов
        page.retrieveExtendedClientDetails(details -> {
            adjustLayout(details.getBodyClientWidth(), desktopView, mobileView);
        });

        // Обработчик изменения размера окна
        page.addBrowserWindowResizeListener(event -> adjustLayout(event.getWidth(), desktopView, mobileView));
    }

    public <T extends NamedEntity> HorizontalLayout getNameLine(T entity,
                                                                CRUDService<T> service,
                                                                BiConsumer<T, String> updateFunction) {
        H1 title = new H1(entity.getName());
        HorizontalLayout titleLine = new HorizontalLayout(title);

        if (permissionsCheck.needEditor()) {
            TextField titleEditor = createEditableField(entity, title, "^.+$", "Boş ola bilməz", updateFunction);
            titleEditor.addClassName("my-text-field");
            titleLine.add(titleEditor);
            titleEditor.setWidthFull();

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH)); // Используем иконку корзины
            deleteButton.addClickListener(e -> showConfirmationDialog(() -> deleteProject(entity, service)));

            deleteButton.addClassNames("text-error", "icon-error", "small-button");

            titleLine.add(deleteButton);

            titleLine.setAlignItems(FlexComponent.Alignment.CENTER);
        }

        return titleLine;
    }

    public HorizontalLayout getAllCommonViewHeader(String headerText, Runnable addFunction) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        var headerName = new H1(headerText);
        horizontalLayout.add(headerName);

        if (permissionsCheck.isEditorOrHigher() && addFunction != null) {
            var addButton = getAddButton(addFunction);
            horizontalLayout.add(addButton);
        }

        return horizontalLayout;
    }

    public void setEmptyFieldStyle(HtmlContainer field, String text) {
        field.setText(text);
        field.getElement().getStyle().set("color", "#4fc3f7");
        field.getElement().getClassList().add("italic");
    }

    public void resetFieldStyle(HtmlContainer field, String text) {
        field.setText(text);
        field.getElement().getStyle().remove("color");
        field.getElement().getClassList().remove("italic");
    }

    public void downloadFile(StreamResource resource) {


        Anchor downloadLink = new Anchor(resource, "Download");
        downloadLink.getElement().setAttribute("download", true);
        UI.getCurrent().getPage().executeJs("window.location.href=$0", downloadLink.getHref());
    }


    private H4 getInformationH4(String information) {
        H4 informationElement = new H4(information.isEmpty() ? "Əlavə et" : information);
        if (information.isEmpty()) {
            informationElement.getElement().getStyle().set("color", "#4fc3f7");
            informationElement.getElement().getClassList().add("italic");
        }
        return informationElement;
    }

    private <T extends NamedEntity> void deleteProject(T entity, CRUDService<T> CRUDService) {
        CRUDService.delete(entity);
        clientListUpdateCommandEventPublisher.updateClientList();
        navigationTools.navigateTo(ViewsEnum.CLIENT);
    }

    private <T extends NamedEntity> TextField createEditableField(T entity,
                                                                  HtmlContainer displayComponent,
                                                                  String regex,
                                                                  String errorMessage,
                                                                  BiConsumer<T, String> updateFunction) {
        TextField editField = new TextField();
        editField.setVisible(false);
        editField.setPattern(regex);
        editField.setErrorMessage(errorMessage);
        editField.setWidth("250px");

        editField.addBlurListener(e -> processEntityUpdate(entity, editField, displayComponent, updateFunction));
        editField.addKeyDownListener(Key.ENTER, e -> processEntityUpdate(entity, editField, displayComponent, updateFunction));
        editField.addKeyDownListener(Key.ESCAPE, e -> {
            displayComponent.setVisible(true);
            editField.setVisible(false);
        });


        displayComponent.getElement().addEventListener("click", e -> {
            String informationText = ((HtmlContainer) displayComponent).getText();
            editField.setValue(informationText.equals("Əlavə et") ? "" : informationText);
            displayComponent.setVisible(false);
            editField.setVisible(true);
            editField.focus();
        });

        return editField;
    }

    private void adjustLayout(int newWidth, Component desktopView, Component mobileView) {
        boolean isDesktopLayout = newWidth > 800;

        // Переключаем видимость макетов
        desktopView.setVisible(isDesktopLayout);
        mobileView.setVisible(!isDesktopLayout);
    }
}
