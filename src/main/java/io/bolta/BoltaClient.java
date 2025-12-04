package io.bolta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.bolta.exception.BoltaApiException;
import io.bolta.exception.BoltaException;
import io.bolta.http.BoltaHttpHeader;
import io.bolta.http.HttpClient;
import io.bolta.http.HttpClients;
import io.bolta.http.HttpHeaders;
import io.bolta.http.HttpRequest;
import io.bolta.http.HttpResponse;
import io.bolta.model.BoltaApiKey;
import io.bolta.model.RequestOptions;
import io.bolta.model.RetryOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Low-level HTTP client for the Bolta API.
 * 볼타 API를 위한 저수준 HTTP 클라이언트입니다.
 * <p>
 * This class handles:
 * 이 클래스는 다음을 처리합니다:
 * <ul>
 * <li>HTTP request/response processing (HTTP요청/응답 처리)</li>
 * <li>Authentication with API key (API 키를 사용한 인증)</li>
 * <li>JSON serialization/deserialization (JSON 직렬화/역직렬화)</li>
 * <li>Error handling (오류 처리)</li>
 * </ul>
 * <p>
 * Most users should use {@link BoltaApp} instead of this class directly.
 * 대부분의 사용자는 이 클래스를 직접 사용하는 대신 {@link BoltaApp}을 사용해야 합니다.
 */
public final class BoltaClient {
    private static final Logger logger = LoggerFactory.getLogger(BoltaClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final BoltaApiKey apiKey;

    /**
     * Builder for constructing BoltaClient instances.
     * <p>
     * BoltaClient 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private HttpClient httpClient;
        private ObjectMapper objectMapper;
        private String baseUrl;
        private BoltaApiKey apiKey;

        /**
         * Sets a custom HTTP client implementation.
         * <p>
         * If not set, a default OkHttp-based client will be used.
         * <p>
         * 커스텀 HTTP 클라이언트 구현을 설정합니다.
         * 설정하지 않으면 기본 OkHttp 기반 클라이언트가 사용됩니다.
         *
         * @param httpClient the HTTP client implementation
         * @return this builder
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Sets a custom ObjectMapper for JSON serialization/deserialization.
         * <p>
         * If not set, a default ObjectMapper will be used.
         * <p>
         * JSON 직렬화/역직렬화를 위한 커스텀 ObjectMapper를 설정합니다.
         * 설정하지 않으면 기본 ObjectMapper가 사용됩니다.
         *
         * @param objectMapper the ObjectMapper to use
         * @return this builder
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Sets the base URL for the Bolta API.
         * <p>
         * 볼타 API의 기본 URL을 설정합니다.
         *
         * @param baseUrl the base URL (e.g., "https://xapi.bolta.io")
         * @return this builder
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the API key for authentication.
         * <p>
         * 인증을 위한 API 키를 설정합니다.
         *
         * @param apiKey the API key
         * @return this builder
         */
        public Builder apiKey(BoltaApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Builds a new BoltaClient instance.
         * <p>
         * 새 BoltaClient 인스턴스를 생성합니다.
         *
         * @return the configured BoltaClient
         * @throws IllegalArgumentException if required fields are missing
         */
        public BoltaClient build() {
            if (apiKey == null) {
                throw new IllegalArgumentException("API key is required");
            }

            return new BoltaClient(this);
        }
    }

    /**
     * Creates a new builder for constructing a BoltaClient instance.
     * <p>
     * BoltaClient 인스턴스를 생성하기 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    private static ObjectMapper defaultObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
    }

    private BoltaClient(Builder builder) {
        this.objectMapper = builder.objectMapper != null ? builder.objectMapper : defaultObjectMapper();
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;

        if (builder.httpClient != null) {
            this.httpClient = builder.httpClient;
        } else {
            this.httpClient = HttpClients.createDefault();
        }
    }

    /**
     * Returns the ObjectMapper used for JSON serialization/deserialization.
     * <p>
     * JSON 직렬화/역직렬화에 사용되는 ObjectMapper를 반환합니다.
     *
     * @return the ObjectMapper instance
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Builds a full URL from the base URL and a path template with arguments.
     * <p>
     * 기본 URL과 경로 템플릿, 인자를 사용하여 전체 URL을 생성합니다.
     *
     * @param path the path template (e.g., "/v1/customers/%s")
     * @param args arguments to substitute into the path template
     * @return the full URL
     */
    public String buildUrl(String path, Object... args) {
        String formattedPath = String.format(path, args);
        return baseUrl + formattedPath;
    }

    /**
     * Executes a request synchronously and deserializes the response.
     * <p>
     * 요청을 동기적으로 실행하고 응답을 역직렬화합니다.
     *
     * @param request      the HTTP request
     * @param responseType the class of the response object
     * @param <T>          the type of the response object
     * @return the deserialized response object
     * @throws BoltaApiException if the API returns an error response
     * @throws BoltaException    if a network or serialization error occurs
     */
    public <T> T execute(HttpRequest request, Class<T> responseType) {
        return execute(request, responseType, null);
    }

    /**
     * Executes an HTTP request and deserializes the response with retry support.
     * HTTP 요청을 실행하고 재시도 지원과 함께 응답을 역직렬화합니다.
     *
     * @param request      the HTTP request to execute
     * @param responseType the expected response type class
     * @param options      request options including retry configuration
     * @param <T>          the response type
     * @return the deserialized response object
     * @throws BoltaApiException if the API returns an error response
     * @throws BoltaException    if a network or serialization error occurs after
     *                           all retries
     */
    public <T> T execute(HttpRequest request, Class<T> responseType, RequestOptions options) {
        HttpHeaders headers = buildHeaders(options);

        HttpRequest newRequest = request.newBuilder()
                .headers(headers)
                .build();

        logger.debug("Executing API request: {} {}", newRequest.getMethod(), newRequest.getUrl());

        RetryOption effectiveRetryOption = (options != null) ? options.getRetryOption() : null;
        int maxAttempts = (effectiveRetryOption != null) ? effectiveRetryOption.getMaxAttempts() : 1;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (attempt > 1) {
                    long delay;
                    delay = effectiveRetryOption.calculateDelayWithJitter(attempt - 1);

                    logger.warn("Retrying request (attempt {}/{}): {} {}",
                            attempt, maxAttempts, newRequest.getMethod(), newRequest.getUrl());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BoltaException("Retry interrupted", ie);
                    }
                }

                return executeOnce(newRequest, responseType);

            } catch (IOException ioException) {
                lastException = ioException;
                boolean shouldRetry = effectiveRetryOption != null
                        && effectiveRetryOption.shouldRetry(attempt, null, true);

                if (attempt < maxAttempts && shouldRetry) {
                    logger.warn("Network error on attempt {}/{}: {}",
                            attempt, maxAttempts, ioException.getMessage());
                } else {
                    logger.error("Network error occurred while executing request: {} {} (attempt {}/{})",
                            newRequest.getMethod(), newRequest.getUrl(), attempt, maxAttempts, ioException);
                    throw new BoltaException("Network error occurred after " + attempt + " attempt(s)", ioException);
                }
            } catch (BoltaApiException exception) {
                boolean shouldRetry = effectiveRetryOption != null
                        && effectiveRetryOption.shouldRetry(attempt, exception.getStatusCode(), false);

                if (attempt < maxAttempts && shouldRetry) {
                    logger.warn("API error on attempt {}/{}, status {}, will retry: {}",
                            attempt, maxAttempts, exception.getStatusCode(), exception.getMessage());
                    lastException = exception;
                    continue;
                }

                throw exception;
            } catch (BoltaException exception) {
                logger.error("Bolta SDK error on attempt {}/{}: {}", attempt, maxAttempts, exception.getMessage(),
                        exception);
                throw exception;
            }
        }

        throw new BoltaException("Request failed after " + maxAttempts + " attempts", lastException);
    }

    private <T> T executeOnce(HttpRequest request, Class<T> responseType) throws IOException, BoltaApiException {
        HttpResponse response = httpClient.execute(request);

        if (!response.isSuccessful()) {
            String body = response.getBody() != null ? response.getBody() : "";
            throw new BoltaApiException(
                    response.getStatusCode(),
                    "API request failed",
                    body);
        }

        if (response.getBody() == null || response.getBody().isEmpty()) {
            if (responseType == Void.class || responseType == void.class) {
                return null;
            }
            throw new BoltaException("Empty response");
        }

        try {
            return objectMapper.readValue(response.getBody(), responseType);
        } catch (Exception exception) {
            throw new BoltaException("Failed to parse response", exception);
        }
    }

    /**
     * Enqueues an asynchronous request and handles the callback.
     * <p>
     * 비동기 요청을 큐에 넣고 콜백을 처리합니다.
     *
     * @param request      the HTTP request
     * @param responseType the class of the response object
     * @param future       the CompletableFuture to complete
     * @param <T>          the type of the response object
     */
    public <T> void enqueueRequest(HttpRequest request, Class<T> responseType, CompletableFuture<T> future) {
        enqueueRequest(request, responseType, null, future);
    }

    /**
     * Enqueues an asynchronous request and handles the callback with options.
     * <p>
     * 옵션과 함께 비동기 요청을 큐에 넣고 콜백을 처리합니다.
     *
     * @param request      the HTTP request
     * @param responseType the class of the response object
     * @param options      request options
     * @param future       the CompletableFuture to complete
     * @param <T>          the type of the response object
     */
    public <T> void enqueueRequest(HttpRequest request, Class<T> responseType, RequestOptions options,
            CompletableFuture<T> future) {
        HttpHeaders headers = buildHeaders(options);

        HttpRequest newRequest = request.newBuilder()
                .headers(headers)
                .build();

        httpClient.executeAsync(newRequest).whenComplete((response, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(new BoltaException("Network error", throwable));
                return;
            }

            try {
                if (!response.isSuccessful()) {
                    String body = response.getBody() != null ? response.getBody() : "";
                    future.completeExceptionally(new BoltaApiException(
                            response.getStatusCode(),
                            "API request failed",
                            body));
                    return;
                }
                if (response.getBody() == null || response.getBody().isEmpty()) {
                    if (responseType == Void.class || responseType == void.class) {
                        future.complete(null);
                        return;
                    }
                    future.completeExceptionally(new BoltaException("Empty response"));
                    return;
                }
                T result = objectMapper.readValue(response.getBody(), responseType);
                future.complete(result);
            } catch (Exception exception) {
                future.completeExceptionally(new BoltaException("Failed to parse response", exception));
            }
        });
    }

    private HttpHeaders buildHeaders(RequestOptions options) {
        HttpHeaders headers = new HttpHeaders();

        String credentials = apiKey.getValue() + ":";
        String encoded = java.util.Base64.getEncoder()
                .encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        headers.add(BoltaHttpHeader.AUTHORIZATION, "Basic " + encoded);
        headers.add(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON_UTF8);

        if (options != null && options.getHeaders() != null) {
            headers.addAll(options.getHeaders());
        }

        return headers;
    }
}
