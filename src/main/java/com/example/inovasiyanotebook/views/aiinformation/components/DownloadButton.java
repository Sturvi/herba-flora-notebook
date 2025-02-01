package com.example.inovasiyanotebook.views.aiinformation.components;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.aiinformation.components.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.SessionScope;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
@Slf4j
@SessionScope
public class DownloadButton {
    private final DesignTools designTools;
    private final ProductService productService;

    private Button button;
    private List<ProductDTO> products;

    @PostConstruct
    public void init() {
        button = designTools.getNewIconButton(VaadinIcon.DOWNLOAD.create(), this::setupDownloadLink);
        products = productService.getAllProductsWithExtraInfo();
    }

private void setupDownloadLink() {
    try {
        // Преобразуем список продуктов в отформатированный JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(products);

        // Создаем временный файл
        File tempFile = File.createTempFile("products", ".json");
        Anchor downloadLink = getAnchor(tempFile, json);
        downloadLink.getElement().setAttribute("download", "products.json");

        // Добавляем ссылку на страницу и имитируем клик
        UI.getCurrent().add(downloadLink); // Явно добавляем в UI
        UI.getCurrent().getPage().executeJs("setTimeout(() => { $0.click(); $0.remove(); }, 100);", downloadLink.getElement());

        log.info("File generated and ready for download: {}", tempFile.getAbsolutePath());
    } catch (Exception e) {
        log.error("Error creating file for download: ", e);
    }
}

    private static Anchor getAnchor(File tempFile, String json) throws IOException {
        tempFile.deleteOnExit(); // Удаляем файл при завершении приложения
        try (FileWriter writer = new FileWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(json);
        }

        // Создаем StreamResource для скачивания файла
        StreamResource resource = new StreamResource("products.json", () -> {
            try {
                return new FileInputStream(tempFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Файл не найден: " + e.getMessage(), e);
            }
        });
        resource.setContentType("application/json");
        resource.setCacheTime(0);

        // Создаем Anchor для загрузки
        return new Anchor(resource, "Download");
    }


    public Button getComponent() {
        return button;
    }
}
