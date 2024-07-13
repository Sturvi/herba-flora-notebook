package com.example.inovasiyanotebook.service.viewservices.product.technicalreview;

import com.example.inovasiyanotebook.model.Product;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@UIScope
public class TechnicalReviewUploader {

    @Getter
    private Upload upload;
    @Setter
    private Product product;

    private final FileUploadService fileUploadService;

    @PostConstruct
    public void init() {
        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setWidthFull();
        upload.setAcceptedFileTypes("application/pdf");

        Button uploadButton = new Button("Rəy yüklə", VaadinIcon.UPLOAD.create());
        upload.setUploadButton(uploadButton);

        upload.addSucceededListener(event -> {
            try {
                ByteArrayResource resource = new ByteArrayResource(buffer.getInputStream().readAllBytes()) {
                    @Override
                    public String getFilename() {
                        return event.getFileName();
                    }
                };

                var response = fileUploadService.uploadFile(resource, product);
                upload.clearFileList();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File processing error", e);
            }
            upload.clearFileList();
        });
    }
}
