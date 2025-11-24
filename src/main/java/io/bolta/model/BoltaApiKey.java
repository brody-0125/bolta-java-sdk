package io.bolta.model;

import java.util.Objects;

/**
 * Represents a Bolta API Key.
 * <p>
 * 볼타 API 키를 나타냅니다.
 * <p>
 * API keys must start with either "live_" or "test_".
 * API 키는 "live_" 또는 "test_"로 시작해야 합니다.
 */
public final class BoltaApiKey {
    private final String value;

    private BoltaApiKey(String value) {
        this.value = value;
    }

    /**
     * Creates a BoltaApiKey from a string value.
     * <p>
     * 문자열 값에서 BoltaApiKey를 생성합니다.
     *
     * @param value the API key string
     * @return the BoltaApiKey instance
     * @throws IllegalArgumentException if the key is invalid
     */
    public static BoltaApiKey of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        if (!value.startsWith("live_") && !value.startsWith("test_")) {
            throw new IllegalArgumentException("API key must start with 'live_' or 'test_'");
        }
        return new BoltaApiKey(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BoltaApiKey apiKey = (BoltaApiKey) o;
        return Objects.equals(value, apiKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
