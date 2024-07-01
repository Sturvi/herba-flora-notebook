package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.service.viewservices.order.worddocument.DocumentParser;
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

    private final DocumentParser documentParser;

    /**
     * Создает компонент загрузки файла.
     *
     * @return объект Upload для загрузки файла.
     */
    public Upload getUpload() {
        log.trace("Создание компонента загрузки файла.");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setUploadButton(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));

        log.trace("Добавление обработчика успешной загрузки.");
        upload.addSucceededListener(event -> {
            log.debug("Загрузка файла завершена: {}", event.getFileName());
            documentParser.processDocument(event.getFileName(), buffer);
            log.trace("Обработка загруженного документа завершена.");
        });

        log.trace("Компонент загрузки файла успешно создан.");
        return upload;
    }
}
