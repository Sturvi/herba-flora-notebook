package com.example.inovasiyanotebook.service.pdf.document;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfDocumentService {

    private static final float FONT_SIZE = 10;

    public void addTitle(PDPageContentStream contentStream, PDType0Font font, float yStart, RawOrderData orderData) throws IOException {
        contentStream.setFont(font, FONT_SIZE + 2);
        contentStream.beginText();
        contentStream.newLineAtOffset(30 + 90, yStart);
        contentStream.showText("İstehsal sifarişi № " + orderData.getOrderNumber() + "           Tarix " + orderData.getOrderDate());
        contentStream.endText();
    }

    public void addDepartment(PDPageContentStream contentStream, PDType0Font font, float yStart) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(30, yStart);
        contentStream.showText("Şöbə: Poliqrafiya şöbəsi");
        contentStream.endText();
    }
}
