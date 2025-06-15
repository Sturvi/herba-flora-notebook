package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@UIScope
public class PriceListUploader extends Upload {
    private final PriceListHandler priceListHandler;
    private final NavigationTools navigationTools;

    @PostConstruct
    private void init() {
        MemoryBuffer buffer = new MemoryBuffer();
        this.setReceiver(buffer);
        this.setDropAllowed(true);
        setUploadButton(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));
        this.setAcceptedFileTypes(".xlsx", ".xls", ".csv");
        this.setWidthFull();
        this.setHeightFull();

        this.addSucceededListener(event -> {
            log.debug("File upload succeeded: {}", event.getFileName());
            try {
                priceListHandler.handlePriceList(buffer);
            } catch (PriceListException e) {
                navigationTools.reloadPage();
            } catch (Exception e) {
                log.error("Error processing price list: {}", e.getMessage(), e);
                Notification.show("Ошибка при обработке прайс-листа: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
            log.trace("File processing completed.");
        });
    }
}
