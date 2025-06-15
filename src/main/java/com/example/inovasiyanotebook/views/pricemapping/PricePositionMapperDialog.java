package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductPriceMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class PricePositionMapperDialog extends Dialog {
    private final DesignTools designTools;
    private final ProductService productService;
    private final ProductPriceMappingService productPriceMappingService;

    private ProductPriceMapping productPriceMapping;
    @Setter
    private Runnable onSaveCallback;

    private TextField incomingNameField;
    private ComboBox<Product> productComboBox;
    private Button saveButton;
    private Button cancelButton;

    @PostConstruct
    private void init() {
        setWidth("700px");
        //setHeight("400px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        incomingNameField = designTools.createTextField("Price List-də ad", null, null);
        incomingNameField.setReadOnly(true);
        productComboBox = designTools.creatComboBox("Məhsul", productService.getAll(), Product::getName, null);

        saveButton = new Button("Yadda saxla");
        saveButton.addClickListener(this::save);
        cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(buttonClickEvent -> this.close());

        add(
                incomingNameField,
                productComboBox,
                new HorizontalLayout(saveButton, cancelButton)
        );
    }

    private void save(ClickEvent<Button> buttonClickEvent) {
        if (checkValidation()) {
            productPriceMapping.setProduct(productComboBox.getValue());

            productPriceMappingService.update(productPriceMapping);
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            this.close();
        }
    }

    public void setProductPriceMapping(ProductPriceMapping productPriceMapping) {
        this.productPriceMapping = productPriceMapping;

        incomingNameField.setValue(productPriceMapping.getIncomingOrderPositionName());
        if (productPriceMapping.getProduct() != null) {
            productComboBox.setValue(productPriceMapping.getProduct());
        } else {
            productComboBox.clear();
        }
    }

    @Override
    public void open() {
        if (productPriceMapping != null) {
            super.open();
        } else {
            log.error("ProductPriceMapping is not set before opening the dialog");
            Notification.show("Error. Please connect with developer.", 3000, Notification.Position.MIDDLE);
        }
    }

    private boolean checkValidation(){
        boolean isValidat = true;

        if (productComboBox.getValue() == null) {
            isValidat = false;
            productComboBox.setErrorMessage("Məhsul seçilməyib");
            productComboBox.setInvalid(true);
        }

        return isValidat;
    }
}
