package io.bolta;

import io.bolta.model.BoltaApiKey;

import java.util.Objects;

/**
 * Configuration for the Bolta SDK.
 * 볼타 SDK의 구성입니다.
 * <p>
 * This class holds all configuration parameters needed to initialize the SDK,
 * including API credentials and timeout settings.
 * 이 클래스는 API 자격 증명 및 타임아웃 설정을 포함하여 SDK를 초기화하는 데 필요한
 * 모든 구성 매개변수를 보유합니다.
 */
public final class BoltaConfig {
    /**
     * API key for authentication
     * 인증을 위한 API 키
     */
    private final BoltaApiKey apiKey;

    /**
     * Base URL for the Bolta API (default: https://xapi.bolta.io)
     * 볼타 API의 기본 URL (기본값: https://xapi.bolta.io)
     */
    private final String baseUrl;

    /**
     * Connection timeout in milliseconds (default: 10000ms)
     * 연결 타임아웃 (밀리초 단위, 기본값: 10000ms)
     */
    private final long connectTimeoutMillis;

    /**
     * Read timeout in milliseconds (default: 30000ms)
     * 읽기 타임아웃 (밀리초 단위, 기본값: 30000ms)
     */
    private final long readTimeoutMillis;

    /**
     * Write timeout in milliseconds (default: 30000ms)
     * 쓰기 타임아웃 (밀리초 단위, 기본값: 30000ms)
     */
    private final long writeTimeoutMillis;

    private BoltaConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.writeTimeoutMillis = builder.writeTimeoutMillis;
    }

    /**
     * Returns the API key for authentication.
     * <p>
     * 인증을 위한 API 키를 반환합니다.
     *
     * @return the API key
     */
    public BoltaApiKey getApiKey() {
        return apiKey;
    }

    /**
     * Returns the base URL for the Bolta API.
     * <p>
     * 볼타 API의 기본 URL을 반환합니다.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the connection timeout in milliseconds.
     * <p>
     * 연결 타임아웃을 밀리초 단위로 반환합니다.
     *
     * @return the connection timeout in milliseconds
     */
    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    /**
     * Returns the read timeout in milliseconds.
     * <p>
     * 읽기 타임아웃을 밀리초 단위로 반환합니다.
     *
     * @return the read timeout in milliseconds
     */
    public long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    /**
     * Returns the write timeout in milliseconds.
     * <p>
     * 쓰기 타임아웃을 밀리초 단위로 반환합니다.
     *
     * @return the write timeout in milliseconds
     */
    public long getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    /**
     * Creates a new builder for constructing a BoltaConfig instance.
     * <p>
     * BoltaConfig 인스턴스를 생성하기 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing BoltaConfig instances.
     * <p>
     * BoltaConfig 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private BoltaApiKey apiKey;
        private String baseUrl = "https://xapi.bolta.io";
        private long connectTimeoutMillis = 10000;
        private long readTimeoutMillis = 30000;
        private long writeTimeoutMillis = 30000;

        /**
         * Sets the API key from a string value.
         * <p>
         * 문자열 값으로 API 키를 설정합니다.
         *
         * @param apiKey the API key string
         * @return this builder
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = BoltaApiKey.of(apiKey);
            return this;
        }

        /**
         * Sets the API key.
         * <p>
         * API 키를 설정합니다.
         *
         * @param apiKey the API key
         * @return this builder
         */
        public Builder apiKey(BoltaApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the connection timeout in milliseconds.
         * <p>
         * 연결 타임아웃을 밀리초 단위로 설정합니다.
         *
         * @param connectTimeoutMillis the connection timeout in milliseconds
         * @return this builder
         */
        public Builder connectTimeoutMillis(long connectTimeoutMillis) {
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        /**
         * Sets the read timeout in milliseconds.
         * <p>
         * 읽기 타임아웃을 밀리초 단위로 설정합니다.
         *
         * @param readTimeoutMillis the read timeout in milliseconds
         * @return this builder
         */
        public Builder readTimeoutMillis(long readTimeoutMillis) {
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        /**
         * Sets the write timeout in milliseconds.
         * <p>
         * 쓰기 타임아웃을 밀리초 단위로 설정합니다.
         *
         * @param writeTimeoutMillis the write timeout in milliseconds
         * @return this builder
         */
        public Builder writeTimeoutMillis(long writeTimeoutMillis) {
            this.writeTimeoutMillis = writeTimeoutMillis;
            return this;
        }

        /**
         * Builds a new BoltaConfig instance.
         * <p>
         * 새 BoltaConfig 인스턴스를 생성합니다.
         *
         * @return the configured BoltaConfig
         * @throws IllegalArgumentException if required fields are missing
         */
        public BoltaConfig build() {
            if (apiKey == null) {
                throw new IllegalArgumentException("API key cannot be null");
            }

            return new BoltaConfig(this);
        }
    }

    @Override
    public String toString() {
        return "BoltaConfig{" +
                "apiKey='" + (apiKey != null ? "*****" : "null") + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", connectTimeoutMillis=" + connectTimeoutMillis +
                ", readTimeoutMillis=" + readTimeoutMillis +
                ", writeTimeoutMillis=" + writeTimeoutMillis +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BoltaConfig that = (BoltaConfig) o;
        return connectTimeoutMillis == that.connectTimeoutMillis &&
                readTimeoutMillis == that.readTimeoutMillis &&
                writeTimeoutMillis == that.writeTimeoutMillis &&
                Objects.equals(apiKey, that.apiKey) &&
                Objects.equals(baseUrl, that.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey, baseUrl, connectTimeoutMillis, readTimeoutMillis, writeTimeoutMillis);
    }
}
