package com.example.inovasiyanotebook.views.pricemapping;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PriceListParserService {
    private static final String NAME_HEADER = "malın adı";
    private static final String PRICE_HEADER = "qiymət";

    /**
     * Парсит Excel файл из MemoryBuffer и возвращает список позиций с ценами
     *
     * @param buffer буфер с загруженным Excel файлом
     * @return список объектов PricePositionDTO
     * @throws IOException если произошла ошибка при чтении файла
     */
    public List<PricePositionDTO> parseExcelFile(MemoryBuffer buffer) throws IOException {
        List<PricePositionDTO> pricePositions = new ArrayList<>();

        String fileName = buffer.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("Имя файла не может быть null");
        }

        try (InputStream inputStream = buffer.getInputStream();
             Workbook workbook = createWorkbook(inputStream, fileName)) {

            Sheet sheet = workbook.getSheetAt(0); // Берем первый лист
            Iterator<Row> rowIterator = sheet.iterator();

            // Колонки названия и цены определяем по строке заголовков, а не по фиксированным индексам
            int nameCol = -1;
            int priceCol = -1;
            while (rowIterator.hasNext() && (nameCol < 0 || priceCol < 0)) {
                Row row = rowIterator.next();
                nameCol = findColumn(row, NAME_HEADER);
                priceCol = findColumn(row, PRICE_HEADER);
            }

            if (nameCol < 0 || priceCol < 0) {
                throw new IllegalArgumentException("Qiymət siyahısında 'Malın adı' / 'Qiymət' başlıqları tapılmadı");
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                PricePositionDTO position = parseRow(row, nameCol, priceCol);

                if (position != null) {
                    pricePositions.add(position);
                }
            }
        }

        return pricePositions;
    }

    /**
     * Ищет в строке ячейку с заданным заголовком
     *
     * @return индекс колонки или -1, если заголовок не найден
     */
    private int findColumn(Row row, String header) {
        for (Cell cell : row) {
            if (header.equals(getCellValueAsString(cell).trim().toLowerCase())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    /**
     * Создает объект Workbook в зависимости от типа файла
     */
    private Workbook createWorkbook(InputStream inputStream, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла: " + fileName +
                    ". Поддерживаются только .xlsx и .xls");
        }
    }

    /**
     * Парсит строку Excel и извлекает информацию о позиции и цене
     *
     * @param row      строка Excel
     * @param nameCol  индекс колонки с названием
     * @param priceCol индекс колонки с ценой
     * @return объект PricePositionDTO или null, если строка не содержит товар с ценой
     */
    private PricePositionDTO parseRow(Row row, int nameCol, int priceCol) {
        if (row == null) {
            return null;
        }

        // Получаем ячейки для названия товара и цены
        Cell nameCell = row.getCell(nameCol);
        Cell priceCell = row.getCell(priceCol);

        // Проверяем, что обе ячейки содержат данные
        if (nameCell == null || priceCell == null) {
            return null;
        }

        String positionName = getCellValueAsString(nameCell);
        String priceValue = getCellValueAsString(priceCell);

        // Фильтруем строки: должно быть и название, и цена
        if (isEmptyOrBlank(positionName) || isEmptyOrBlank(priceValue)) {
            return null;
        }

        // Пропускаем заголовки и категории
        if (isHeaderOrCategory(positionName, priceValue)) {
            return null;
        }

        // Очищаем название от лишних пробелов и отступов
        String cleanedName = cleanPositionName(positionName);

        // Проверяем, что это реальный товар (не пустое название после очистки)
        if (isEmptyOrBlank(cleanedName)) {
            return null;
        }

        // Форматируем цену
        String formattedPrice = formatPrice(priceValue);

        return new PricePositionDTO(cleanedName, formattedPrice);
    }

    /**
     * Получает значение ячейки как строку
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    // Форматируем числа без лишних нулей
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        yield String.valueOf((long) numValue);
                    } else {
                        yield String.valueOf(numValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        yield String.valueOf((long) numValue);
                    } else {
                        yield String.valueOf(numValue);
                    }
                } catch (Exception e) {
                    yield cell.getStringCellValue();
                }
            }
            default -> "";
        };
    }

    /**
     * Проверяет, является ли строка заголовком или категорией
     */
    private boolean isHeaderOrCategory(String positionName, String priceValue) {
        String lowerName = positionName.toLowerCase().trim();
        String lowerPrice = priceValue.toLowerCase().trim();

        // Список ключевых слов для заголовков и категорий
        String[] headerKeywords = {
                "прайс", "price", "цена", "номенклатура", "ценовая группа",
                "hazır məhsul", "bitki", "çaylar", "böyük ölçülü", "ətirli çaylar",
                "malın adı", "qiymət"
        };

        for (String keyword : headerKeywords) {
            if (lowerName.contains(keyword) || lowerPrice.contains(keyword)) {
                return true;
            }
        }

        // Проверяем, является ли цена текстом (не числом)
        try {
            Double.parseDouble(priceValue.trim());
            return false; // Это число, значит не заголовок
        } catch (NumberFormatException e) {
            return true; // Это текст в поле цены, значит заголовок или категория
        }
    }

    /**
     * Очищает название позиции от лишних пробелов и отступов
     */
    private String cleanPositionName(String positionName) {
        if (positionName == null) {
            return "";
        }

        return positionName.trim();
    }

    /**
     * Форматирует цену
     */
    private String formatPrice(String priceValue) {
        if (priceValue == null || priceValue.trim().isEmpty()) {
            return "0";
        }

        try {
            // Пытаемся преобразовать в число и отформатировать
            double price = Double.parseDouble(priceValue.trim());

            // Если цена целая, убираем десятичные разряды
            if (price == Math.floor(price)) {
                var result = String.valueOf((long) price);
                result = result + " AZN";
                return result;
            } else {
                // Ограничиваем до 2 знаков после запятой
                var result = String.format("%.2f", price);
                result = result.replace(",", ".") + " AZN";
                return result;
            }
        } catch (NumberFormatException e) {
            // Если не удалось преобразовать, возвращаем как есть
            return priceValue.trim();
        }
    }

    /**
     * Проверяет, является ли строка пустой или содержит только пробелы
     */
    private boolean isEmptyOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}