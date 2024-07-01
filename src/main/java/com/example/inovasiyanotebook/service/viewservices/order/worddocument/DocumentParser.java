package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.RawOrderDataService;
import com.example.inovasiyanotebook.service.pdf.PdfGeneratorService;
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
import java.io.IOException;
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
    private final RawOrderDataService rawOrderDataService;
    private final OrderCreationService orderCreationService;
    private final PdfGeneratorService pdfGeneratorService;

    /**
     * Обрабатывает загруженный документ, парсит его и выполняет соответствующие действия в зависимости от содержимого.
     *
     * @param fileName имя файла.
     * @param buffer   буфер, содержащий содержимое файла.
     */
    @Override
    public void processDocument(String fileName, MemoryBuffer buffer) {
        try {
            // Создание временного файла из загруженного буфера
            Path tempFile = fileService.createTempDocOrDocxFile(fileName, buffer.getInputStream());
            Notification.show("Файл успешно загружен: " + tempFile);

            // Парсинг данных заказа из файла
            RawOrderData rawOrderData = documentService.parseOrderFromFile(tempFile);

            // Проверка на наличие неизвестных позиций заказа
            if (documentService.hasUnknownOrderPositions(rawOrderData.getPositions())) {
                navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
            } else {
                orderCreationService.createNewOrder(rawOrderData);
                rawOrderData.setIsProcessed(true);
            }

            // Сохранение данных заказа в базе данных
            rawOrderDataService.create(rawOrderData);

            // Отображение диалога для печати PDF-документа
            showPrintDialog(pdfGeneratorService.generatePdf(rawOrderData).toPath());
        } catch (FileService.InvalidFileTypeException e) {
            Notification.show("Ошибка: загрузка возможна только для файлов Word");
        } catch (Exception e) {
            log.error("Ошибка при обработке файла: " + e.getMessage(), e);
            Notification.show("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    /**
     * Отображает диалоговое окно для печати PDF-документа.
     *
     * @param tempFile путь к временному файлу PDF-документа.
     */
    private void showPrintDialog(Path tempFile) {
        Dialog dialog = createPrintDialog(tempFile);
        dialog.open();

        dialog.getElement().executeJs(
                "const dialog = this;" +
                        "dialog.style.position = 'fixed';" +
                        "dialog.style.bottom = '0';" +
                        "dialog.style.right = '0';"
        );
    }

    /**
     * Создает диалоговое окно для печати PDF-документа.
     *
     * @param tempFile путь к временному файлу PDF-документа.
     * @return созданное диалоговое окно.
     */
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
            if (Files.exists(tempFile)) {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    log.error("Ошибка при удалении временного файла: " + e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                log.trace("Временный файл успешно удален.");
            }
        });
        yesButton.getStyle().set("margin-right", "10px");

        Button noButton = new Button("Xeyr", event -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(yesButton, noButton);
        buttons.setSpacing(true);

        content.add(message, buttons);
        dialog.add(content);

        return dialog;
    }

    /**
     * Открывает PDF-документ для печати.
     *
     * @param pdfFilePath путь к PDF-документу.
     */
    public void openPdfForPrinting(Path pdfFilePath) {
        try {
            byte[] pdfContent = Files.readAllBytes(pdfFilePath);
            openPdfInNewWindow(pdfContent);
        } catch (IOException e) {
            log.error("Ошибка при чтении и открытии PDF: " + e.getMessage(), e);
            Notification.show("Ошибка при чтении и открытии PDF: " + e.getMessage());
        }
    }

    /**
     * Открывает PDF-документ в новом окне.
     *
     * @param pdfContent содержимое PDF-документа в виде массива байтов.
     */
    private void openPdfInNewWindow(byte[] pdfContent) {
        StreamResource resource = new StreamResource("document.pdf", () -> new ByteArrayInputStream(pdfContent));
        resource.setContentType("application/pdf");
        resource.setCacheTime(0);

        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        String resourceUrl = registration.getResourceUri().toString();
        UI.getCurrent().getPage().open(resourceUrl, "_blank");
    }
}
