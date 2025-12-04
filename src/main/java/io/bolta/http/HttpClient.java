package io.bolta.http;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for HTTP client implementations.
 * <p>
 * HTTP 클라이언트 구현을 위한 인터페이스입니다.
 */
public interface HttpClient {
    /**
     * Executes an HTTP request synchronously.
     * <p>
     * HTTP 요청을 동기적으로 실행합니다.
     *
     * @param request the HTTP request
     * @return the HTTP response
     * @throws IOException if an I/O error occurs
     */
    HttpResponse execute(HttpRequest request) throws IOException;

    /**
     * Executes an HTTP request asynchronously.
     * <p>
     * HTTP 요청을 비동기적으로 실행합니다.
     *
     * @param request the HTTP request
     * @return a CompletableFuture containing the HTTP response
     */
    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
