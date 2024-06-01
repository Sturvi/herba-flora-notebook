package com.example.inovasiyanotebook.service;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class WordToPdfConverter {

    public byte[] convertToPdf(InputStream docxInputStream) throws Exception {
        try (XWPFDocument docx = new XWPFDocument(docxInputStream);
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {

            // Ensure document body and section properties are not null
            CTBody body = docx.getDocument().getBody();
            if (body == null) {
                body = docx.getDocument().addNewBody();
            }

            CTSectPr sectPr = body.getSectPr();
            if (sectPr == null) {
                sectPr = body.addNewSectPr();
            }

            CTPageSz pageSize = sectPr.getPgSz();
            if (pageSize == null) {
                pageSize = sectPr.addNewPgSz();
                pageSize.setW(11906);  // Default page width in twips (A4 size)
                pageSize.setH(16838);  // Default page height in twips (A4 size)
            }

            // Proceed with PDF conversion
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(docx, pdfOutputStream, options);

            return pdfOutputStream.toByteArray();
        }
    }
}
