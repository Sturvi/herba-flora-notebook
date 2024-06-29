package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.service.WordToPdfConverter;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.entityservices.iml.RawOrderDataService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
@UIScope
public class DocumentParser {
    private final ProductMappingService productMappingService;
    private final NavigationTools navigationTools;
    private final NewOrderDialog newOrderDialog;
    private final WordToPdfConverter wordToPdfConverter;
    private final RawOrderDataService rawOrderDataService;

    public void processDocument(String fileName, MemoryBuffer buffer) {
        try {
            Path tempFile = createTempFile(fileName, buffer.getInputStream());
            Notification.show("Файл успешно загружен: " + tempFile);
            parseOrderFromFile(tempFile);

            // Открытие диалогового окна
            //showPrintDialog(tempFile);
        } catch (InvalidFileTypeException e) {
            Notification.show("Ошибка: загрузка возможна только для файлов Word");
        } catch (Exception e) {
            log.error("Ошибка при обработке файла: " + e.getMessage(), e);
            Notification.show("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void showPrintDialog(Path tempFile) {
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
        dialog.open();
    }


    private void openPdfForPrinting(Path tempFile) {
        try {
            byte[] pdfContent = wordToPdfConverter.convertToPdf(Files.newInputStream(tempFile));
            openPdfInNewWindow(pdfContent);
        } catch (Exception e) {
            log.error("Ошибка при конвертации и открытии PDF: " + e.getMessage(), e);
            Notification.show("Ошибка при конвертации и открытии PDF: " + e.getMessage());
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

    private Path createTempFile(String fileName, InputStream inputStream) throws Exception {
        if (!fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
            throw new InvalidFileTypeException("Неверный тип файла: " + fileName);
        }

        Path tempFile = Files.createTempFile(null, fileName);
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    private void parseOrderFromFile(Path file) {
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(file))) {
            List<XWPFTable> tables = document.getTables();
            if (tables.isEmpty()) {
                throw new RuntimeException("Таблицы в документе не найдены");
            }

            processTable(tables.get(0));
        } catch (Exception e) {
            log.error("Ошибка при анализе файла: " + e.getMessage(), e);
        }
    }

    private void processTable(XWPFTable table) {
        if (table.getRows().size() < 2) {
            throw new IllegalStateException("Таблица не содержит достаточно строк для обработки заказа");
        }

        RawOrderData rawOrderData = new RawOrderData();

        extractOrderInfoFromFirstRow(rawOrderData, table);

        boolean hasUnknownPosition = extractAndProcessOrderPositions(table, rawOrderData);

        if (hasUnknownPosition) {
            navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
        } else {
            createNewOrder(rawOrderData);
            rawOrderData.setIsProcessed(true);
        }

        rawOrderDataService.create(rawOrderData);
    }

    private void extractOrderInfoFromFirstRow(RawOrderData rawOrderData, XWPFTable table) {
        XWPFTableRow headerRow = table.getRow(0);
        extractOrderInfoFromRow(rawOrderData, headerRow);
    }

    private boolean extractAndProcessOrderPositions(XWPFTable table, RawOrderData rawOrderData) {
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            try {
                Optional<RawPositionData> documentOrderPositionOptional = parseOrderItem(row);
                if (documentOrderPositionOptional.isPresent()) {
                    RawPositionData orderPosition = documentOrderPositionOptional.get();
                    rawOrderData.addPosition(orderPosition);
                }
            } catch (OrderPositionsExhaustedException e) {
                if (rawOrderData.hasPosition()) break;
            }
        }
        return hasUnknownOrderPositions(rawOrderData.getPositions());
    }


    private void createNewOrder(RawOrderData rawOrderData) {
        Order order = createOrder(rawOrderData);
        List<OrderPosition> orderPositions = createOrderPositions(rawOrderData.getPositions(), order);

        if (orderPositions.isEmpty()) {
            // Если нет позиций заказа, возможно, следует предпринять какие-то действия
            navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
            return;
        }

        order.setOrderPositions(orderPositions);
        newOrderDialog.openNewDialog(order);
    }

    private Order createOrder(RawOrderData rawOrderData) {
        Order order = new Order();
        order.setOrderNo(rawOrderData.getOrderNumber());
        order.setOrderReceivedDateTime(LocalDateTime.of(rawOrderData.getOrderDate(), LocalTime.now()));
        order.setStatus(OrderStatusEnum.OPEN);
        return order;
    }

    private List<OrderPosition> createOrderPositions(List<RawPositionData> documentOrderPositions, Order order) {
        return documentOrderPositions.stream()
                .flatMap(documentOrderPosition -> toOrderPosition(documentOrderPosition, order).stream())
                .toList();
    }

    private Optional<OrderPosition> toOrderPosition(RawPositionData documentOrderPosition, Order order) {
        return productMappingService.findByIncomingOrderPositionName(documentOrderPosition.getPositionName())
                .map(productMapping -> OrderPosition.builder()
                        .order(order)
                        .product(productMapping.getProduct())
                        .printedType(productMapping.getPrintedType())
                        .status(OrderStatusEnum.OPEN)
                        .count(documentOrderPosition.getQuantity().toString())
                        .comment(productMapping.getComment() + " " + documentOrderPosition.getShelfLife() + " " + documentOrderPosition.getNote())
                        .build());
    }


    private void extractOrderInfoFromRow(RawOrderData rawOrderData, XWPFTableRow row) {
        Pattern pattern = Pattern.compile("İstehsal sifarişi № (\\d+) tarix (\\d{2}\\.\\d{2}\\.\\d{4})");
        for (XWPFTableCell cell : row.getTableCells()) {
            String cellText = cell.getText();
            Matcher matcher = pattern.matcher(cellText);
            if (matcher.find()) {
                Integer orderNo = Integer.parseInt(matcher.group(1));
                LocalDate orderDate = LocalDate.parse(matcher.group(2), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                rawOrderData.setOrderNumber(orderNo);
                rawOrderData.setOrderDate(orderDate);
                return;
            }
        }
        throw new IllegalStateException("Не удалось извлечь информацию о заказе");
    }


    private Optional<RawPositionData> parseOrderItem(XWPFTableRow row) {
        List<XWPFTableCell> cells = row.getTableCells();

        if (cells.size() > 6 && cells.get(1).getText().matches("^\\d+$")) {
            try {
                Integer orderNo = Integer.valueOf(cells.get(1).getText());
                String name = cells.get(2).getText();
                Integer quantity = Integer.valueOf(cells.get(3).getText().replaceAll("\\s", "").replaceAll("\u00A0", ""));
                String shelfLife = cells.get(5).getText();
                String comment = cells.get(6).getText();

                return Optional.of(new RawPositionData(orderNo, name, quantity, shelfLife, comment));
            } catch (NumberFormatException e) {
                log.error("Ошибка при парсинге количества: " + e.getMessage());
                return Optional.empty();
            }
        } else {
            throw new OrderPositionsExhaustedException("Orders ended");
        }
    }
    private boolean hasUnknownOrderPositions(List<RawPositionData> positions) {
        boolean hasUnknownPosition = false;
        for (RawPositionData orderPosition : positions) {
            if (isUnknownOrderPosition(orderPosition)) {
                hasUnknownPosition = true;
            }
        }
        return hasUnknownPosition;
    }

    private boolean isUnknownOrderPosition(RawPositionData orderPosition) {
        var productMappingOpt = productMappingService.findByIncomingOrderPositionName(orderPosition.getPositionName());

        if (productMappingOpt.isEmpty()) {
            createProductMapping(orderPosition);
            return true;
        } else {
            return productMappingOpt.get().getProduct() == null;
        }
    }

    private void createProductMapping(RawPositionData orderPosition) {
        var productMapping = ProductMapping.builder()
                .incomingOrderPositionName(orderPosition.getPositionName())
                .build();
        productMappingService.create(productMapping);
        System.out.println("Created new ProductMapping for position: " + orderPosition.getPositionName()); // logging
    }

    public static class OrderPositionsExhaustedException extends RuntimeException {
        public OrderPositionsExhaustedException(String message) {
            super(message);
        }
    }

    public static class InvalidFileTypeException extends Exception {
        public InvalidFileTypeException(String message) {
            super(message);
        }
    }
}
