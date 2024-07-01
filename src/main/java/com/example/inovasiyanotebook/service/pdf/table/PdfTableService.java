package com.example.inovasiyanotebook.service.pdf.table;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfTableService {

    private static final float TABLE_HEIGHT = 15;
    private static final float FONT_SIZE = 10;

    public void drawTableHeader(PDPageContentStream contentStream, PDType0Font font, float yStart, float[] colWidths) throws IOException {
        String[] headers = {"№", "Məhsul", "Say", "ÖV", "Sax. müddəti", "Qeyd"};
        contentStream.setFont(font, FONT_SIZE);
        contentStream.setLineWidth(0.75f);

        float xStart = 30;

        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(xStart, yStart, colWidths[i], TABLE_HEIGHT);
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + 2, yStart + 5);
            contentStream.showText(headers[i]);
            contentStream.endText();
            xStart += colWidths[i];
        }
        contentStream.stroke();
    }

    public void drawTableRow(PDPageContentStream contentStream, PDType0Font font, float yStart, float[] colWidths, String[] row) throws IOException {
        float xStart = 30;

        for (int i = 0; i < row.length; i++) {
            contentStream.addRect(xStart, yStart, colWidths[i], TABLE_HEIGHT);
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + 2, yStart + 5);
            String text = row[i];
            if (text != null) {
                contentStream.showText(text);
            }
            contentStream.endText();
            xStart += colWidths[i];
        }
        contentStream.stroke();
    }
}
