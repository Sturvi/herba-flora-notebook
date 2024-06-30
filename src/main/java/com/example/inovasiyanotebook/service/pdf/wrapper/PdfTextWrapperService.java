package com.example.inovasiyanotebook.service.pdf.wrapper;

import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfTextWrapperService {

    public List<String[]> wrapRowText(RawPositionData position, float[] colWidths, PDType0Font font, float fontSize) throws IOException {
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
                float lineWidth = font.getStringWidth(line.toString() + " " + word) / 1000 * fontSize;
                if (lineWidth > colWidths[i] - 4) {
                    wrappedText.add(line.toString());
                    line = new StringBuilder(word);
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

        return wrappedRows;
    }
}
