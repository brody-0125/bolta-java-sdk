package io.bolta.http;

/**
 * Constants for HTTP headers used in the Bolta SDK.
 * <p>
 * Bolta SDK에서 사용되는 HTTP 헤더 상수입니다.
 */
public final class BoltaHttpHeader {
    private BoltaHttpHeader() {
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CUSTOMER_KEY = "Customer-Key";
    public static final String BOLTA_CLIENT_REFERENCE_ID = "Bolta-Client-Reference-Id";

    public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    public static final String APPLICATION_JSON = "application/json";
}
