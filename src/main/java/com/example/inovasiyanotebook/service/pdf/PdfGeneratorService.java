package com.example.inovasiyanotebook.service.pdf;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.service.pdf.document.PdfDocumentService;
import com.example.inovasiyanotebook.service.pdf.table.PdfTableService;
import com.example.inovasiyanotebook.service.pdf.wrapper.PdfTextWrapperService;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PdfGeneratorService {

    private static final float MARGIN = 30;
    private static final float TABLE_HEIGHT = 15;
    private static final float FONT_SIZE = 8;

    private final PdfDocumentService pdfDocumentService;
    private final PdfTableService pdfTableService;
    private final PdfTextWrapperService pdfTextWrapperService;

    public PdfGeneratorService(PdfDocumentService pdfDocumentService, PdfTableService pdfTableService, PdfTextWrapperService pdfTextWrapperService) {
        this.pdfDocumentService = pdfDocumentService;
        this.pdfTableService = pdfTableService;
        this.pdfTextWrapperService = pdfTextWrapperService;
    }

    public File generatePdf(RawOrderData orderData) throws IOException {
        File tempFile = File.createTempFile("order_", ".pdf");
        try (PDDocument document = new PDDocument()) {
            PDType0Font font = loadFont(document, "fonts/Arial Unicode MS.ttf");

            PDPage page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yStart = page.getMediaBox().getHeight() - MARGIN;

                pdfDocumentService.addTitle(contentStream, font, yStart, orderData);
                pdfDocumentService.addDepartment(contentStream, font, yStart - 20);
                yStart -= 70;

                float[] colWidths = {30, 250, 50, 30, 100, 80};

                pdfTableService.drawTableHeader(contentStream, font, yStart, colWidths);
                yStart -= TABLE_HEIGHT;

                for (RawPositionData position : orderData.getPositions()) {
                    List<String[]> wrappedRows = pdfTextWrapperService.wrapRowText(position, colWidths, font, FONT_SIZE);
                    for (String[] row : wrappedRows) {
                        if (yStart < MARGIN + TABLE_HEIGHT) {
                            contentStream.close();
                            page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
                            document.addPage(page);
                            try (PDPageContentStream newContentStream = new PDPageContentStream(document, page)) {
                                newContentStream.setFont(font, FONT_SIZE);
                                yStart = page.getMediaBox().getHeight() - MARGIN;
                                pdfTableService.drawTableHeader(newContentStream, font, yStart, colWidths);
                                yStart -= TABLE_HEIGHT;
                                pdfTableService.drawTableRow(newContentStream, font, yStart, colWidths, row);
                                yStart -= TABLE_HEIGHT;
                            }
                        } else {
                            pdfTableService.drawTableRow(contentStream, font, yStart, colWidths, row);
                            yStart -= TABLE_HEIGHT;
                        }
                    }
                }
            }
            document.save(tempFile);
        }

        return tempFile;
    }

    private PDType0Font loadFont(PDDocument document, String fontPath) throws IOException {
        try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                throw new IOException("Font file not found: " + fontPath);
            }
            return PDType0Font.load(document, fontStream);
        }
    }
}
