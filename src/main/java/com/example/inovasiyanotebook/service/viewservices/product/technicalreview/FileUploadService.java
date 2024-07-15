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
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final WebClient.Builder webClientBuilder;

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
        String url = "http://172.17.0.1:25000/api/files/download?name=" + product.getName() + "&category=" + product.getCategory().getName() + "&documentType=" + "TECHNICAL_REVIEW";

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .block();
    }

    public ResponseEntity<String> renameFile(String name, String category, String documentType, String newName) {
        String url = "http://localhost:25000/api/files/rename?name=" + name + "&category=" + category + "&documentType=" + documentType + "&newName=" + newName;

        return webClientBuilder.build()
                .put()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> deleteFile(String name, String category, String documentType) {
        String url = "http://localhost:25000/api/files/delete?name=" + name + "&category=" + category + "&documentType=" + documentType;

        return webClientBuilder.build()
                .delete()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> changeCategory(String name, String category, String documentType, String newCategory) {
        String url = "http://localhost:25000/api/files/changeCategory?name=" + name + "&category=" + category + "&documentType=" + documentType + "&newCategory=" + newCategory;

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
