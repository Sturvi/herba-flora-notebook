package com.example.inovasiyanotebook.service.pdf.wrapper;

import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PdfTextWrapperService {

    // Добавим запас для переноса строки
    private static final float LINE_WRAP_MARGIN = 5.0f;

    /**
     * Переносит текст в строке таблицы в зависимости от ширины столбцов.
     *
     * @param position данные позиции
     * @param colWidths ширины столбцов
     * @param font шрифт, используемый в PDF
     * @param fontSize размер шрифта
     * @return список строк с перенесённым текстом
     * @throws IOException если происходит ошибка ввода-вывода
     */
    public List<String[]> wrapRowText(RawPositionData position, float[] colWidths, PDType0Font font, float fontSize) throws IOException {
        log.trace("Начало переноса текста для позиции: {}", position);

        List<String[]> wrappedRows = new ArrayList<>();

        String[] row = {
                position.getPositionNo() != null ? position.getPositionNo().toString() : "",
                position.getPositionName() != null ? position.getPositionName() : "",
                position.getQuantity() != null ? position.getQuantity().toString() : "",
                "əd",
                position.getShelfLife() != null ? position.getShelfLife() : "",
                position.getNote() != null ? position.getNote() : ""
        };

        for (int i = 0; i < row.length; i++) {
            List<String> wrappedText = new ArrayList<>();
            String[] words = row[i].split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                // Вычисляем ширину строки с добавленным словом
                float lineWidth = font.getStringWidth(line.toString() + (line.length() > 0 ? " " : "") + word) / 1000 * fontSize;
                log.trace("Ширина строки '{} {}': {} (макс: {})", line, word, lineWidth, colWidths[i] - 4 - LINE_WRAP_MARGIN);

                if (lineWidth > colWidths[i] - 4 - LINE_WRAP_MARGIN) {
                    // Если текущая строка не помещается, переносим её
                    wrappedText.add(line.toString());
                    line = new StringBuilder(word);
                    log.trace("Перенос строки в столбце {}: {}", i, line);
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
            wrappedText.add(line.toString());

            while (wrappedText.size() > wrappedRows.size()) {
                wrappedRows.add(new String[row.length]);
            }
            for (int j = 0; j < wrappedText.size(); j++) {
                wrappedRows.get(j)[i] = wrappedText.get(j);
            }
        }

        log.trace("Текст после переноса: {}", wrappedRows);
        return wrappedRows;
    }
}
