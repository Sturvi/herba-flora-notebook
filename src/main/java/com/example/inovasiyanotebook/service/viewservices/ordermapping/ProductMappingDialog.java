package com.example.inovasiyanotebook.service.viewservices.ordermapping;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.service.entityservices.iml.PrintedTypeService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ProductMappingDialog {
    private final DesignTools designTools;
    private final ProductService productService;
    private final PrintedTypeService printedTypeService;
    private final ProductMappingService productMappingService;

    private TextField incomingNameField;
    private ComboBox<Product> productComboBox;
    private ComboBox<PrintedType> printedTypeComboBox;
    private TextField commentTextField;
    private Button saveButton;
    private Button cancellButton;

    private Dialog dialog;
    private ProductMapping productMapping;


    @PostConstruct
    private void init (){
        incomingNameField = designTools.createTextField("1C-də ad", null, null);
        incomingNameField.setReadOnly(true);
        productComboBox = designTools.creatComboBox("Məhsul", productService.getAll(), Product::getName, null);
        printedTypeComboBox = designTools.creatComboBox("Çap növü", printedTypeService.getAll(), PrintedType::getName, null);
        commentTextField = designTools.createTextField("Not", null, null);

        dialog = new Dialog();
        dialog.setWidth("700px");

        saveButton = new Button("Yadda saxla");
        saveButton.addClickListener(this::save);
        cancellButton = new Button("Ləğv et");
        cancellButton.addClickListener(buttonClickEvent -> dialog.close());


        dialog.add(new VerticalLayout(incomingNameField, productComboBox, printedTypeComboBox, commentTextField, new HorizontalLayout(saveButton, cancellButton)));
    }

    public void setProductMappingAndOpenDialog (ProductMapping productMapping) {
        setProductMappingData(productMapping);
        dialog.open();
    }

    private void setProductMappingData(ProductMapping productMapping) {
        this.productMapping = productMapping;
        incomingNameField.setValue(productMapping.getIncomingOrderPositionName());
        productComboBox.setValue(productMapping.getProduct());
        printedTypeComboBox.setValue(productMapping.getPrintedType());
        commentTextField.setValue(productMapping.getComment() == null ? "" : productMapping.getComment());
    }


    private void save(ClickEvent<Button> buttonClickEvent) {
        if (checkValidation()) {
            productMapping.setProduct(productComboBox.getValue());
            productMapping.setPrintedType(printedTypeComboBox.getValue());
            productMapping.setComment(commentTextField.getValue());
            productMappingService.update(productMapping);
            dialog.close();
        }
    }

    private boolean checkValidation(){
        boolean isValidat = true;

        if (productComboBox.getValue() == null) {
            isValidat = false;
            productComboBox.setErrorMessage("Məhsul seçilməyib");
            productComboBox.setInvalid(true);
        }

        if (printedTypeComboBox.getValue() == null) {
            isValidat = false;
            printedTypeComboBox.setErrorMessage("Çap növü seçilməyib");
            printedTypeComboBox.setInvalid(true);
        }

        return isValidat;
    }
}
