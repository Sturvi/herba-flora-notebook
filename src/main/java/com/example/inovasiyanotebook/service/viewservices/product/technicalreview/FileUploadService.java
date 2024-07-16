package com.example.inovasiyanotebook.service.viewservices.product.technicalreview;

import com.example.inovasiyanotebook.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final WebClient.Builder webClientBuilder;

    public void changeNameOrCategory(String oldName, String newName, String oldCategory, String newCategory) {
        try {
            if (!oldName.equals(newName)) {
                renameFile(oldName, oldCategory, newName);
            }
        } catch (WebClientResponseException e) {
            logException(e, oldName, newName, oldCategory, newCategory);
        }

        try {
            if (!oldCategory.equals(newCategory)) {
                changeCategory(newName, oldCategory, newCategory);
            }
        } catch (WebClientResponseException e) {
            logException(e, oldName, newName, oldCategory, newCategory);
        }
    }

    private void logException(WebClientResponseException e, String oldName, String newName, String oldCategory, String newCategory) {
        if (e.getStatusCode().value() == 404) {
            log.trace("404 Not Found: Unable to process change for file {} from category {} to category {}", newName, oldCategory, newCategory, e);
        } else {
            log.error("Error occurred while processing change for file {}: old name - {}, new name - {}, old category - {}, new category - {}", oldName, oldName, newName, oldCategory, newCategory, e);
        }
    }

    public ResponseEntity<String> uploadFile(ByteArrayResource resource, Product product) {
        String url = "http://172.17.0.1:25000/api/files/upload";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", product.getName());
        body.add("category", product.getCategory().getName());
        body.add("documentType", "TECHNICAL_REVIEW");
        body.add("file", resource);

        log.info("Uploading file for product: {}", product.getName());

        return webClientBuilder.build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .toEntity(String.class)
                .doOnNext(response -> log.info("File upload successful, response: {}", response))
                .doOnError(error -> log.error("Error during file upload", error))
                .block();
    }

    public ResponseEntity<ByteArrayResource> downloadFile(Product product) {
        String url = "http://172.17.0.1:25000/api/files/download?name=" + product.getName() + "&category=" + product.getCategory().getName() + "&documentType=TECHNICAL_REVIEW";

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)) // 10MB buffer
                .build();

        return webClientBuilder
                .exchangeStrategies(strategies)
                .build()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .block();
    }

    public ResponseEntity<String> renameFile(String name, String category, String newName) {
        String url = "http://172.17.0.1:25000/api/files/rename?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW" + "&newName=" + newName;

        return webClientBuilder.build()
                .put()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> deleteFile(String name, String category) {
        String url = "http://172.17.0.1:25000/api/files/delete?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW";

        return webClientBuilder.build()
                .delete()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> changeCategory(String name, String category, String newCategory) {
        String url = "http://172.17.0.1:25000/api/files/changeCategory?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW" + "&newCategory=" + newCategory;

        return webClientBuilder.build()
                .put()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<Boolean> fileExists(Product product) {
        String url = "http://172.17.0.1:25000/api/files/exists?name=" + product.getName() + "&category=" + product.getCategory().getName() + "&documentType=" + "TECHNICAL_REVIEW";

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(Boolean.class)
                .block();
    }
}
