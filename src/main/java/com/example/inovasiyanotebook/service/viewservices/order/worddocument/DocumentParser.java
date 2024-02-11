package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public void processDocument(String fileName, MemoryBuffer buffer) {
        try {
            Path tempFile = createTempFile(fileName, buffer.getInputStream());
            Notification.show("Файл успешно загружен: " + tempFile);
            parseOrderFromFile(tempFile);
        } catch (InvalidFileTypeException e) {
            Notification.show("Ошибка: загрузка возможна только для файлов Word");
        } catch (Exception e) {
            log.error("Ошибка при обработке файла: " + e.getMessage(), e);
            Notification.show("Ошибка при загрузке файла: " + e.getMessage());
        }
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

        DocumentOrderInformation documentOrderInformation = extractOrderInfoFromFirstRow(table)
                .orElseThrow(() -> new IllegalStateException("Не удалось извлечь информацию о заказе"));

        List<DocumentOrderPosition> documentOrderPositions = new ArrayList<>();
        boolean hasUnknownPosition = extractAndProcessOrderPositions(table, documentOrderPositions);

        if (hasUnknownPosition) {
            navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
        } else {
            createNewOrder(documentOrderPositions, documentOrderInformation);
        }
    }

    private Optional<DocumentOrderInformation> extractOrderInfoFromFirstRow(XWPFTable table) {
        XWPFTableRow headerRow = table.getRow(0);
        return extractOrderInfoFromRow(headerRow);
    }

    private boolean extractAndProcessOrderPositions(XWPFTable table, List<DocumentOrderPosition> documentOrderPositions) {
        boolean hasUnknownPosition = false;
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            try {
                Optional<DocumentOrderPosition> documentOrderPositionOptional = parseOrderItem(row);
                if (documentOrderPositionOptional.isPresent()) {
                    DocumentOrderPosition orderPosition = documentOrderPositionOptional.get();
                    hasUnknownPosition |= processOrderPosition(orderPosition);
                    documentOrderPositions.add(orderPosition);
                }
            } catch (OrderPositionsExhaustedException e) {
                if (!documentOrderPositions.isEmpty()) break;
            }
        }
        return hasUnknownPosition;
    }



    private void createNewOrder(List<DocumentOrderPosition> documentOrderPositions, DocumentOrderInformation documentOrderInformation) {
        Order order = createOrder(documentOrderInformation);
        List<OrderPosition> orderPositions = createOrderPositions(documentOrderPositions, order);

        if (orderPositions.isEmpty()) {
            // Если нет позиций заказа, возможно, следует предпринять какие-то действия
            navigationTools.navigateTo(ViewsEnum.PRODUCT_MAPPING);
            return;
        }

        order.setOrderPositions(orderPositions);
        newOrderDialog.openNewDialog(order);
    }

    private Order createOrder(DocumentOrderInformation documentOrderInformation) {
        Order order = new Order();
        order.setOrderNo(documentOrderInformation.getOrderNo());
        order.setOrderReceivedDateTime(LocalDateTime.of(documentOrderInformation.getOrderDate(), LocalTime.now()));
        order.setStatus(OrderStatusEnum.OPEN);
        return order;
    }

    private List<OrderPosition> createOrderPositions(List<DocumentOrderPosition> documentOrderPositions, Order order) {
        return documentOrderPositions.stream()
                .flatMap(documentOrderPosition -> toOrderPosition(documentOrderPosition, order).stream())
                .toList();
    }

    private Optional<OrderPosition> toOrderPosition(DocumentOrderPosition documentOrderPosition, Order order) {
        return productMappingService.findByIncomingOrderPositionName(documentOrderPosition.getName())
                .map(productMapping -> OrderPosition.builder()
                        .order(order)
                        .product(productMapping.getProduct())
                        .printedType(productMapping.getPrintedType())
                        .status(OrderStatusEnum.OPEN)
                        .count(documentOrderPosition.getQuantity().toString())
                        .comment(productMapping.getComment() + " " + documentOrderPosition.getComment())
                        .build());
    }


    private Optional<DocumentOrderInformation> extractOrderInfoFromRow(XWPFTableRow row) {
        Pattern pattern = Pattern.compile("İstehsal sifarişi № (\\d+) tarix (\\d{2}\\.\\d{2}\\.\\d{4})");
        for (XWPFTableCell cell : row.getTableCells()) {
            String cellText = cell.getText();
            Matcher matcher = pattern.matcher(cellText);
            if (matcher.find()) {
                Integer orderNo = Integer.parseInt(matcher.group(1));
                LocalDate orderDate = LocalDate.parse(matcher.group(2), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                return Optional.of(new DocumentOrderInformation(orderNo, orderDate));
            }
        }
        return Optional.empty();
    }


    private Optional<DocumentOrderPosition> parseOrderItem(XWPFTableRow row) {
        List<XWPFTableCell> cells = row.getTableCells();

        if (cells.size() > 6 && cells.get(1).getText().matches("^\\d+$")) {
            String name = cells.get(2).getText();
            String quantity = cells.get(3).getText().replaceAll("\\s","").replaceAll("\u00A0","");
            String comment = cells.get(6).getText();

            try {
                int quantityInt = Integer.parseInt(quantity);
                return Optional.of(new DocumentOrderPosition(name, quantityInt, comment));
            } catch (NumberFormatException e) {
                log.error("Ошибка при парсинге количества: " + e.getMessage());
                return Optional.empty();
            }
        } else {
            throw new OrderPositionsExhaustedException("Orders ended");
        }
    }

    private boolean processOrderPosition(DocumentOrderPosition orderPosition) {
        var productMappingOpt = productMappingService.findByIncomingOrderPositionName(orderPosition.getName());

        if (productMappingOpt.isEmpty()) {
            var productMapping = ProductMapping.builder()
                    .incomingOrderPositionName(orderPosition.getName())
                    .build();
            productMappingService.create(productMapping);
            return true;
        } else return productMappingOpt.get().getProduct() == null;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class DocumentOrderPosition {
        private final String name;
        private final Integer quantity;
        private final String comment;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class DocumentOrderInformation {
        private final Integer orderNo;
        private final LocalDate orderDate;
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
