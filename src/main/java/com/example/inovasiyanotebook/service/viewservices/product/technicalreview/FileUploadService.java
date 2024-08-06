package com.example.inovasiyanotebook.service.viewservices.product.technicalreview;

import com.example.inovasiyanotebook.model.Product;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final WebClient.Builder webClientBuilder;
    private final AtomicBoolean isServiceAvailable = new AtomicBoolean(true);

    @Value("${file.service.url}")
    private String fileServiceUrl;

    @PostConstruct
    public void init() {
        checkServiceAvailability();
    }

    @Scheduled(fixedRate = 30000)
    public void checkServiceAvailability() {
        if (!isServiceAvailable.get()) {
            log.info("Checking service availability...");
            try {
                Boolean available = testServiceAvailability().block();
                if (Boolean.TRUE.equals(available)) {
                    isServiceAvailable.set(true);
                    log.info("Service is now available.");
                }
            } catch (Exception e) {
                log.error("Service availability check failed", e);
            }
        }
    }

    private Mono<Boolean> testServiceAvailability() {
        String url = fileServiceUrl + "/api/files/test";

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(false);
    }

    public void changeNameOrCategory(String oldName, String newName, String oldCategory, String newCategory) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        try {
            if (!oldName.equals(newName)) {
                renameFile(oldName, oldCategory, newName);
            }
        } catch (WebClientResponseException | WebClientRequestException e) {
            handleWebClientException(e, oldName, newName, oldCategory, newCategory);
        }

        try {
            if (!oldCategory.equals(newCategory)) {
                changeCategory(newName, oldCategory, newCategory);
            }
        } catch (WebClientResponseException | WebClientRequestException e) {
            handleWebClientException(e, oldName, newName, oldCategory, newCategory);
        }
    }

    private void handleWebClientException(Exception e, String oldName, String newName, String oldCategory, String newCategory) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException we = (WebClientResponseException) e;
            if (we.getStatusCode().value() == 404) {
                log.trace("404 Not Found: Unable to process change for file {} from category {} to category {}", newName, oldCategory, newCategory, we);
            } else if (we.getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
                log.error("Service unavailable while processing change for file {}: old name - {}, new name - {}, old category - {}, new category - {}", oldName, oldName, newName, oldCategory, newCategory, we);
            } else {
                log.error("Error occurred while processing change for file {}: old name - {}, new name - {}, old category - {}, new category - {}", oldName, oldName, newName, oldCategory, newCategory, we);
            }
        } else if (e instanceof WebClientRequestException) {
            isServiceAvailable.set(false);
            log.error("Request error while processing change for file {}: old name - {}, new name - {}, old category - {}, new category - {}", oldName, oldName, newName, oldCategory, newCategory, e);
        } else {
            log.error("Unexpected error occurred while processing change for file {}: old name - {}, new name - {}, old category - {}, new category - {}", oldName, oldName, newName, oldCategory, newCategory, e);
        }
    }

    public ResponseEntity<String> uploadFile(ByteArrayResource resource, Product product) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/upload";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", product.getName());
        body.add("category", product.getCategory().getName());
        body.add("documentType", "TECHNICAL_REVIEW");
        body.add("file", resource);

        log.info("Uploading file for product: {}", product.getName());

        try {
            return webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error during file upload", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file upload", e);
            throw e;
        }
    }

    public ResponseEntity<ByteArrayResource> downloadFile(Product product) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/download?name=" + product.getName() + "&category=" + product.getCategory().getName() + "&documentType=TECHNICAL_REVIEW";

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(30 * 1024 * 1024)) // 10MB buffer
                .build();

        try {
            return webClientBuilder
                    .exchangeStrategies(strategies)
                    .build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntity(ByteArrayResource.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error during file download", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file download", e);
            throw e;
        }
    }

    public ResponseEntity<String> renameFile(String name, String category, String newName) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/rename?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW" + "&newName=" + newName;

        try {
            return webClientBuilder.build()
                    .put()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error during file rename", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file rename", e);
            throw e;
        }
    }

    public ResponseEntity<String> deleteFile(String name, String category) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/delete?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW";

        try {
            return webClientBuilder.build()
                    .delete()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error during file delete", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file delete", e);
            throw e;
        }
    }

    public ResponseEntity<String> changeCategory(String name, String category, String newCategory) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/changeCategory?name=" + name + "&category=" + category + "&documentType=" + "TECHNICAL_REVIEW" + "&newCategory=" + newCategory;

        try {
            return webClientBuilder.build()
                    .put()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error during file category change", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file category change", e);
            throw e;
        }
    }

    public ResponseEntity<Boolean> fileExists(Product product) {
        if (!isServiceAvailable.get()) {
            throw new IllegalStateException("Service is unavailable");
        }

        String url = fileServiceUrl + "/api/files/exists?name=" + product.getName() + "&category=" + product.getCategory().getName() + "&documentType=" + "TECHNICAL_REVIEW";

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntity(Boolean.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            if (e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()) {
                isServiceAvailable.set(false);
            } else if (e instanceof WebClientRequestException) {
                isServiceAvailable.set(false);
            }
            log.error("Error checking if file exists", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error checking if file exists", e);
            throw e;
        }
    }
}
