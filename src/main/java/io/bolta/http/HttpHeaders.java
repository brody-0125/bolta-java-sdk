package io.bolta.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents HTTP headers for requests.
 * <p>
 * HTTP 요청을 위한 헤더를 나타냅니다.
 */
public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    /**
     * Gets all headers as a map.
     * <p>
     * 모든 헤더를 맵으로 가져옵니다.
     *
     * @return the headers map
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Adds a single header.
     * <p>
     * 단일 헤더를 추가합니다.
     *
     * @param name  the header name
     * @param value the header value
     */
    public void add(String name, String value) {
        if (name != null && value != null) {
            headers.put(name, value);
        }
    }

    /**
     * Adds multiple headers from a map.
     * <p>
     * 맵에서 여러 헤더를 추가합니다.
     *
     * @param headersToAdd the headers to add
     */
    public void addAll(Map<String, String> headersToAdd) {
        if (headersToAdd != null) {
            headers.putAll(headersToAdd);
        }
    }
}
