package io.bolta.model;

import java.util.Objects;

/**
 * Base configuration options for API requests.
 * Contains common options like retry configuration.
 * <p>
 * API 요청을 위한 기본 구성 옵션입니다.
 * 재시도 구성과 같은 공통 옵션을 포함합니다.
 */
public class RequestOptions {
    /**
     * Retry configuration for this request.
     * When set, the client will automatically retry failed requests according to
     * the policy.
     * <p>
     * 이 요청에 대한 재시도 구성입니다.
     * 설정된 경우, 클라이언트는 정책에 따라 실패한 요청을 자동으로 재시도합니다.
     */
    private final RetryOption retryOption;

    /**
     * Custom headers for this request.
     * <p>
     * 이 요청에 대한 사용자 정의 헤더입니다.
     */
    private final java.util.Map<String, String> headers;

    protected RequestOptions(Builder<?> builder) {
        this.retryOption = builder.retryOption;
        this.headers = java.util.Collections.unmodifiableMap(new java.util.HashMap<>(builder.headers));
    }

    public RetryOption getRetryOption() {
        return retryOption;
    }

    public java.util.Map<String, String> getHeaders() {
        return headers;
    }

    public static Builder<?> builder() {
        return new Builder<>();
    }

    /**
     * Builder for constructing RequestOptions instances.
     * <p>
     * RequestOptions 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder<T extends Builder<T>> {
        protected RetryOption retryOption;
        protected java.util.Map<String, String> headers = new java.util.HashMap<>();

        /**
         * Sets the retry policy for this request.
         * <p>
         * 이 요청에 대한 재시도 정책을 설정합니다.
         */
        @SuppressWarnings("unchecked")
        public T retryOption(RetryOption retryOption) {
            this.retryOption = retryOption;
            return (T) this;
        }

        /**
         * Adds a custom header to this request.
         * <p>
         * 이 요청에 사용자 정의 헤더를 추가합니다.
         */
        @SuppressWarnings("unchecked")
        public T header(String key, String value) {
            this.headers.put(key, value);
            return (T) this;
        }

        /**
         * Adds multiple custom headers to this request.
         * <p>
         * 이 요청에 여러 사용자 정의 헤더를 추가합니다.
         */
        @SuppressWarnings("unchecked")
        public T headers(java.util.Map<String, String> headers) {
            this.headers.putAll(headers);
            return (T) this;
        }

        public RequestOptions build() {
            return new RequestOptions(this);
        }
    }

    @Override
    public String toString() {
        return "RequestOptions{" +
                "retryOption=" + retryOption +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RequestOptions that = (RequestOptions) o;
        return Objects.equals(retryOption, that.retryOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retryOption);
    }
}
