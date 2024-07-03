package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.OrderCreationService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
@UIScope
public class DocumentService {

    private final ProductMappingService productMappingService;
    private final NavigationTools navigationTools;
    private final OrderCreationService orderCreationService;

    public RawOrderData parseOrderFromFile(Path file) throws Exception {
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(file))) {
            List<XWPFTable> tables = document.getTables();
            if (tables.isEmpty()) {
                throw new RuntimeException("Таблицы в документе не найдены");
            }

            return processTable(tables.get(0));
        } catch (Exception e) {
            log.error("Ошибка при анализе файла: " + e.getMessage(), e);
            throw e;
        }
    }

    private RawOrderData processTable(XWPFTable table) {
        if (table.getRows().size() < 2) {
            throw new IllegalStateException("Таблица не содержит достаточно строк для обработки заказа");
        }

        RawOrderData rawOrderData = new RawOrderData();
        extractOrderInfoFromRow(rawOrderData, table.getRow(0));
        extractAndProcessOrderPositions(table, rawOrderData);

        return rawOrderData;
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

    private void extractAndProcessOrderPositions(XWPFTable table, RawOrderData rawOrderData) {
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

    public boolean hasUnknownOrderPositions(List<RawPositionData> positions) {
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
        System.out.println("Created new ProductMapping for position: " + orderPosition.getPositionName());
    }

    public static class OrderPositionsExhaustedException extends RuntimeException {
        public OrderPositionsExhaustedException(String message) {
            super(message);
        }
    }
}
