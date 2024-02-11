package com.example.inovasiyanotebook.service.viewservices.order.worddocument;

import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
@Slf4j
public class UploadComponentCreator {
    private final ProductMappingService productMappingService;
    private final NavigationTools navigationTools;
    private final DocumentParser documentParser;

    public Upload getUpload () {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setUploadButton(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));

        upload.addSucceededListener(event -> {documentParser.processDocument(event.getFileName(), buffer);});

        return upload;
    }
}
