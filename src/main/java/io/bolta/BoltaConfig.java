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

    private BoltaConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.baseUrl = builder.baseUrl;
    }

    public BoltaApiKey getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BoltaApiKey apiKey;
        private String baseUrl = "https://xapi.bolta.io";

        public Builder apiKey(String apiKey) {
            this.apiKey = BoltaApiKey.of(apiKey);
            return this;
        }

        public Builder apiKey(BoltaApiKey apiKey) {
            this.apiKey = apiKey;
            return this;
        }

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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BoltaConfig that = (BoltaConfig) o;
        return Objects.equals(apiKey, that.apiKey) &&
                Objects.equals(baseUrl, that.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey, baseUrl);
    }
}
