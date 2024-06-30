package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfGeneratorService {

    private static final float MARGIN = 30;
    private static final float TABLE_HEIGHT = 15;
    private static final float FONT_SIZE = 10;

    public File generatePdf(RawOrderData orderData) throws IOException {
        File tempFile = File.createTempFile("order_", ".pdf");
        try (PDDocument document = new PDDocument()) {
            PDType0Font font = PDType0Font.load(document, new File("src/main/resources/fonts/DejaVuSans.ttf"));

            PDPage page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yStart = page.getMediaBox().getHeight() - MARGIN;

            // Add title
            contentStream.setFont(font, FONT_SIZE + 2);
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN + 90, yStart);
            contentStream.showText("İstehsal sifarişi № " + orderData.getOrderNumber() + "           Tarix " + orderData.getOrderDate());
            contentStream.endText();

            // Add department
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, yStart - 20);
            contentStream.showText("Şöbə: Poliqrafiya şöbəsi");
            contentStream.endText();

            yStart -= 70;

            float[] colWidths = {30, 250, 50, 30, 100, 100}; // Adjust column widths as needed

            // Draw header
            drawTableHeader(contentStream, font, yStart, colWidths);
            yStart -= TABLE_HEIGHT;

            // Add table rows
            for (RawPositionData position : orderData.getPositions()) {
                List<String[]> wrappedRows = wrapRowText(position, colWidths, font, FONT_SIZE);
                for (String[] row : wrappedRows) {
                    if (yStart < MARGIN + TABLE_HEIGHT) {
                        contentStream.close();
                        page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, FONT_SIZE);
                        yStart = page.getMediaBox().getHeight() - MARGIN;
                        // Draw header on new page
                        drawTableHeader(contentStream, font, yStart, colWidths);
                        yStart -= TABLE_HEIGHT;
                    }

                    drawTableRow(contentStream, font, yStart, colWidths, row);
                    yStart -= TABLE_HEIGHT;
                }
            }

            contentStream.close();
            document.save(tempFile);
        }

        return tempFile;
    }


    private void drawTableHeader(PDPageContentStream contentStream, PDType0Font font, float yStart, float[] colWidths) throws IOException {
        String[] headers = {"№", "Məhsul", "Say", "ÖV", "Sax. müddəti", "Qeyd"};
        contentStream.setFont(font, FONT_SIZE);
        contentStream.setLineWidth(0.75f);

        float xStart = MARGIN;

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

    private void drawTableRow(PDPageContentStream contentStream, PDType0Font font, float yStart, float[] colWidths, String[] row) throws IOException {
        float xStart = MARGIN;

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

    private List<String[]> wrapRowText(RawPositionData position, float[] colWidths, PDType0Font font, float fontSize) throws IOException {
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
