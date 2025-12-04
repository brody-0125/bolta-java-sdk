package io.bolta.http;

import io.bolta.http.impl.DefaultHttpClient;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Factory for creating HttpClient instances.
 * <p>
 * This class encapsulates the creation of HTTP clients, hiding the
 * underlying implementation details (OkHttp) from the rest of the SDK.
 * <p>
 * HttpClient 인스턴스를 생성하기 위한 팩토리입니다.
 * <p>
 * 이 클래스는 HTTP 클라이언트 생성을 캡슐화하여 SDK의 나머지 부분에서
 * 기본 구현 세부 사항(OkHttp)을 숨깁니다.
 */
public final class HttpClients {

    private HttpClients() {
        // Utility class
    }

    /**
     * Creates a default HttpClient with standard timeout settings.
     * <p>
     * 표준 타임아웃 설정으로 기본 HttpClient를 생성합니다.
     *
     * @return the default HttpClient instance
     */
    public static HttpClient createDefault() {
        return new DefaultHttpClient(new OkHttpClient());
    }

    /**
     * Creates an HttpClient with custom timeout settings.
     * <p>
     * 커스텀 타임아웃 설정으로 HttpClient를 생성합니다.
     *
     * @param connectTimeoutMillis connection timeout in milliseconds
     * @param readTimeoutMillis    read timeout in milliseconds
     * @param writeTimeoutMillis   write timeout in milliseconds
     * @return the configured HttpClient instance
     */
    public static HttpClient create(long connectTimeoutMillis, long readTimeoutMillis, long writeTimeoutMillis) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMillis, TimeUnit.MILLISECONDS)
                .build();
        return new DefaultHttpClient(okHttpClient);
    }
}
