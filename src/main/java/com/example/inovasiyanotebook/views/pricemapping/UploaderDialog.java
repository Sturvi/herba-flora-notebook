package com.example.inovasiyanotebook.views.pricemapping;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@UIScope
public class UploaderDialog extends Dialog {
    private final PriceListUploader priceListUploader;


    @PostConstruct
    private void init() {
        setWidth("600px");
        setHeight("400px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        this.add(priceListUploader);
    }
}
