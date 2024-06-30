package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.WordToPdfConverter;
import com.example.inovasiyanotebook.service.entityservices.iml.RawOrderDataService;
import com.example.inovasiyanotebook.service.viewservices.order.DocumentService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@UIScope
@Slf4j
@AllArgsConstructor
public class DocumentParser implements DocumentProcessor {

    private final FileService fileService;
    private final DocumentService documentService;
    private final NavigationTools navigationTools;
    private final WordToPdfConverter wordToPdfConverter;
    private final RawOrderDataService rawOrderDataService;
    private final OrderCreationService orderCreationService;

    @Override
    public void processDocument(String fileName, MemoryBuffer buffer) {
        try {
            Path tempFile = fileService.createTempFile(fileName, buffer.getInputStream());
            Notification.show("Fayl uğurla yükləndi: " + tempFile);
            RawOrderData rawOrderData = documentService.parseOrderFromFile(tempFile);

            if (documentService.hasUnknownOrderPositions(rawOrderData.getPositions())) {
                navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
            } else {
                orderCreationService.createNewOrder(rawOrderData);
                rawOrderData.setIsProcessed(true);
            }

            rawOrderDataService.create(rawOrderData);
        } catch (FileService.InvalidFileTypeException e) {
            Notification.show("Xəta: yükləmə yalnız Word faylları üçün mümkündür");
        } catch (Exception e) {
            log.error("Faylın işlənməsi zamanı xəta: " + e.getMessage(), e);
            Notification.show("Faylın yüklənməsi zamanı xəta: " + e.getMessage());
        }
    }

    private void showPrintDialog(Path tempFile) {
        Dialog dialog = createPrintDialog(tempFile);
        dialog.open();
    }

    private Dialog createPrintDialog(Path tempFile) {
        Dialog dialog = new Dialog();
        dialog.getElement().getStyle().set("background", "white");
        dialog.getElement().getStyle().set("padding", "20px");
        dialog.getElement().getStyle().set("border-radius", "8px");
        dialog.getElement().getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        Span message = new Span("Faylı çap etmək üçün açmaq istəyirsiniz?");
        message.getStyle().set("margin-bottom", "20px");

        Button yesButton = new Button("Bəli", event -> {
            dialog.close();
            openPdfForPrinting(tempFile);
        });
        yesButton.getStyle().set("margin-right", "10px");

        Button noButton = new Button("Xeyr", event -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(yesButton, noButton);
        buttons.setSpacing(true);

        content.add(message, buttons);
        dialog.add(content);

        return dialog;
    }

    private void openPdfForPrinting(Path tempFile) {
        try {
            byte[] pdfContent = wordToPdfConverter.convertToPdf(Files.newInputStream(tempFile));
            openPdfInNewWindow(pdfContent);
        } catch (Exception e) {
            log.error("PDF-in konvertasiyası və açılması zamanı xəta: " + e.getMessage(), e);
            Notification.show("PDF-in konvertasiyası və açılması zamanı xəta: " + e.getMessage());
        }
    }

    private void openPdfInNewWindow(byte[] pdfContent) {
        StreamResource resource = new StreamResource("document.pdf", () -> new ByteArrayInputStream(pdfContent));
        resource.setContentType("application/pdf");
        resource.setCacheTime(0);

        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        String resourceUrl = registration.getResourceUri().toString();
        UI.getCurrent().getPage().open(resourceUrl, "_blank");
    }
}
