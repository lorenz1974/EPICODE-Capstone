package capstone.be.restapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import capstone.be.utility.ObjectToJSONConverter;
import reactor.core.publisher.Mono;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestApiClient {

    private final WebClient webClient;

    public String getDataFromApi(String url, String token) {
        log.debug("Executing GET request to: {}", url);

        return webClient.get()
                .uri(url)
                .headers(headers -> setHeaders(headers, token))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("GET request successful: {}", url))
                .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                .onErrorResume(error -> handleUnexpectedError(error, "GET"))
                .block();
    }

    public String postDataToApi(String url, Object requestBody, String token) throws JsonProcessingException {
        log.debug("Executing POST request to: {}", url);
        String requestBodyJson = ObjectToJSONConverter.convert(requestBody);

        return webClient.post()
                .uri(url)
                .headers(headers -> setHeaders(headers, token))
                .body(Mono.just(requestBodyJson), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("POST request successful: {}", url))
                .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                .onErrorResume(error -> handleUnexpectedError(error, "POST"))
                .block();
    }

    public String putDataToApi(String url, Object requestBody, String token) throws JsonProcessingException {
        log.debug("Executing PUT request to: {}", url);
        String requestBodyJson = ObjectToJSONConverter.convert(requestBody);

        return webClient.put()
                .uri(url)
                .headers(headers -> setHeaders(headers, token))
                .body(Mono.just(requestBodyJson), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("PUT request successful: {}", url))
                .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                .onErrorResume(error -> handleUnexpectedError(error, "PUT"))
                .block();
    }

    public String deleteDataFromApi(String url, String token) {
        log.debug("Executing DELETE request to: {}", url);

        return webClient.delete()
                .uri(url)
                .headers(headers -> setHeaders(headers, token))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("DELETE request successful: {}", url))
                .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                .onErrorResume(error -> handleUnexpectedError(error, "DELETE"))
                .block();
    }

    public String postFileToApi(String url, File file, String token) {
        log.debug("Executing POST request for file upload to: {}", url);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileSystemResource(file))
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=" + file.getName());

        return webClient.post()
                .uri(url)
                .headers(headers -> setHeaders(headers, token))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("File uploaded successfully: {}", url))
                .onErrorResume(WebClientResponseException.class, this::handleWebClientError)
                .onErrorResume(error -> handleUnexpectedError(error, "POST File"))
                .block();
    }

    public String postFileToApi(String url, String filePath, String token) {
        log.debug("Reading file from path: {}", filePath);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }
        return postFileToApi(url, file, token);
    }

    private void setHeaders(HttpHeaders headers, String token) {
        headers.set(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (token != null && !token.isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        log.debug("Request Headers: {}", headers);
    }

    private Mono<String> handleWebClientError(WebClientResponseException error) {
        log.error("API call failed with status {}: {}", error.getStatusCode(), error.getResponseBodyAsString());
        return Mono.just(error.getResponseBodyAsString());
    }

    private Mono<String> handleUnexpectedError(Throwable error, String method) {
        log.error("Unexpected error during API {} call: {}", method, error.getMessage());
        return Mono.just("API request failed: " + error.getMessage());
    }
}
