package com.example.inovasiyanotebook.service;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

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
                // Set page size to A5 landscape
            }
            pageSize.setW(11906);  // Width in twips for A5 landscape
            pageSize.setH(8391);   // Height in twips for A5 landscape

            // Set font for all runs to support Azerbaijani characters
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        run.setFontFamily("Arial Unicode MS");  // Example font supporting Azerbaijani characters
                    }
                }
            }

            // Proceed with PDF conversion
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(docx, pdfOutputStream, options);

            return pdfOutputStream.toByteArray();
        }
    }
}
