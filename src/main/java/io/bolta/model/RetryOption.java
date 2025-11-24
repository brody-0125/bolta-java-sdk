package io.bolta.model;

import io.bolta.retry.BackoffStrategy;
import io.bolta.retry.FixedBackoffStrategy;
import io.bolta.retry.StatusCodeMatcher;

import java.util.Objects;

/**
 * Configuration for retry behavior when making API requests.
 * API 요청 시 재시도 동작을 설정합니다.
 * <p>
 * Use the builder to configure retry behavior with different backoff
 * strategies,
 * jitter, and status code filtering.
 * 빌더를 사용하여 다양한 백오프 전략, jitter, 상태 코드 필터링을 설정할 수 있습니다.
 * <p>
 * Example:
 * 
 * <pre>{@code
 * RetryOption option = RetryOption.builder()
 *         .maxAttempts(5)
 *         .exponentialBackoff(1000, 2.0, 30000)
 *         .enableJitter(0.2)
 *         .retryOnStatusCodes(RangeStatusCodeMatcher.of(500, 599))
 *         .build();
 * }</pre>
 */
public final class RetryOption {
    private final int maxAttempts;
    private final BackoffStrategy backoffStrategy;
    private final boolean jitterEnabled;
    private final double jitterFactor;
    private final StatusCodeMatcher statusCodeMatcher;
    private final boolean retryOnNetworkError;

    private RetryOption(Builder builder) {
        this.maxAttempts = builder.maxAttempts;
        this.backoffStrategy = builder.backoffStrategy;
        this.jitterEnabled = builder.jitterEnabled;
        this.jitterFactor = builder.jitterFactor;
        this.statusCodeMatcher = builder.statusCodeMatcher;
        this.retryOnNetworkError = builder.retryOnNetworkError;
    }

    /**
     * Calculates the delay before the next retry attempt, with optional jitter
     * applied.
     * 다음 재시도 전 지연 시간을 계산하며, 선택적으로 jitter를 적용합니다.
     *
     * @param attemptNumber the current attempt number (1-based)
     *                      현재 시도 횟수 (1부터 시작)
     * @return delay in milliseconds
     *         밀리초 단위의 지연 시간
     */
    public long calculateDelayWithJitter(int attemptNumber) {
        long baseDelay = backoffStrategy.calculateDelay(attemptNumber);

        if (!jitterEnabled) {
            return baseDelay;
        }

        // Apply jitter: random value within ±jitterFactor
        // jitter 적용: ±jitterFactor 범위 내의 무작위 값
        double jitter = (Math.random() * 2 - 1) * jitterFactor;
        return (long) (baseDelay * (1 + jitter));
    }

    /**
     * Determines whether a retry should be attempted based on the current state.
     * 현재 상태에 따라 재시도를 수행해야 하는지 결정합니다.
     *
     * @param attemptNumber  the current attempt number (1-based)
     *                       현재 시도 횟수 (1부터 시작)
     * @param statusCode     the HTTP status code, or null for network errors
     *                       HTTP 상태 코드, 네트워크 에러의 경우 null
     * @param isNetworkError true if this is a network error (IOException)
     *                       네트워크 에러(IOException)인 경우 true
     * @return true if a retry should be attempted
     *         재시도를 수행해야 하면 true
     */
    public boolean shouldRetry(int attemptNumber, Integer statusCode, boolean isNetworkError) {
        if (attemptNumber >= maxAttempts) {
            return false;
        }

        if (isNetworkError && retryOnNetworkError) {
            return true;
        }

        if (statusCode != null && statusCodeMatcher != null) {
            return statusCodeMatcher.matches(statusCode);
        }

        return false;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public BackoffStrategy getBackoffStrategy() {
        return backoffStrategy;
    }

    public boolean isJitterEnabled() {
        return jitterEnabled;
    }

    public double getJitterFactor() {
        return jitterFactor;
    }

    public StatusCodeMatcher getStatusCodeMatcher() {
        return statusCodeMatcher;
    }

    public boolean isRetryOnNetworkError() {
        return retryOnNetworkError;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxAttempts = 3;
        private BackoffStrategy backoffStrategy = new FixedBackoffStrategy(1000);
        private boolean jitterEnabled = false;
        private double jitterFactor = 0.1;
        private StatusCodeMatcher statusCodeMatcher;
        private boolean retryOnNetworkError = true;

        private Builder() {
        }

        /**
         * Sets the maximum number of retry attempts.
         * 최대 재시도 횟수를 설정합니다.
         *
         * @param maxAttempts maximum number of attempts (must be at least 1)
         *                    최대 시도 횟수 (최소 1 이상)
         * @return this builder
         */
        public Builder maxAttempts(int maxAttempts) {
            if (maxAttempts < 1) {
                throw new IllegalArgumentException("maxAttempts must be at least 1");
            }
            this.maxAttempts = maxAttempts;
            return this;
        }

        /**
         * Sets a fixed backoff strategy with the specified delay.
         * 지정된 지연 시간을 사용하는 고정 백오프 전략을 설정합니다.
         *
         * @param delayMillis delay in milliseconds
         *                    밀리초 단위의 지연 시간
         * @return this builder
         */
        public Builder fixedBackoff(long delayMillis) {
            this.backoffStrategy = new io.bolta.retry.FixedBackoffStrategy(delayMillis);
            return this;
        }

        /**
         * Sets a random backoff strategy with the specified range.
         * 지정된 범위를 사용하는 랜덤 백오프 전략을 설정합니다.
         *
         * @param minMillis minimum delay in milliseconds
         *                  밀리초 단위의 최소 지연 시간
         * @param maxMillis maximum delay in milliseconds
         *                  밀리초 단위의 최대 지연 시간
         * @return this builder
         */
        public Builder randomBackoff(long minMillis, long maxMillis) {
            this.backoffStrategy = new io.bolta.retry.RandomBackoffStrategy(minMillis, maxMillis);
            return this;
        }

        /**
         * Sets an exponential backoff strategy.
         * 지수 백오프 전략을 설정합니다.
         *
         * @param baseMillis base delay in milliseconds
         *                   밀리초 단위의 기본 지연 시간
         * @param multiplier exponential multiplier
         *                   지수 승수
         * @param maxMillis  maximum delay in milliseconds
         *                   밀리초 단위의 최대 지연 시간
         * @return this builder
         */
        public Builder exponentialBackoff(long baseMillis, double multiplier, long maxMillis) {
            this.backoffStrategy = new io.bolta.retry.ExponentialBackoffStrategy(baseMillis, multiplier, maxMillis);
            return this;
        }

        /**
         * Enables jitter with the specified factor.
         * 지정된 계수로 jitter를 활성화합니다.
         *
         * @param factor jitter factor between 0.0 and 1.0
         *               0.0과 1.0 사이의 jitter 계수
         * @return this builder
         */
        public Builder enableJitter(double factor) {
            if (factor < 0.0 || factor > 1.0) {
                throw new IllegalArgumentException("jitter factor must be between 0.0 and 1.0");
            }
            this.jitterEnabled = true;
            this.jitterFactor = factor;
            return this;
        }

        /**
         * Sets the status code matcher for determining which status codes trigger
         * retries.
         * 어떤 상태 코드가 재시도를 트리거할지 결정하는 매처를 설정합니다.
         *
         * @param matcher the status code matcher
         *                상태 코드 매처
         * @return this builder
         */
        public Builder retryOnStatusCodes(StatusCodeMatcher matcher) {
            this.statusCodeMatcher = matcher;
            return this;
        }

        /**
         * Sets whether to retry on network errors (IOException).
         * 네트워크 에러(IOException) 시 재시도 여부를 설정합니다.
         *
         * @param retryOnNetworkError true to retry on network errors
         *                            네트워크 에러 시 재시도하려면 true
         * @return this builder
         */
        public Builder retryOnNetworkError(boolean retryOnNetworkError) {
            this.retryOnNetworkError = retryOnNetworkError;
            return this;
        }

        public RetryOption build() {
            return new RetryOption(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RetryOption that = (RetryOption) o;
        return maxAttempts == that.maxAttempts &&
                jitterEnabled == that.jitterEnabled &&
                Double.compare(that.jitterFactor, jitterFactor) == 0 &&
                retryOnNetworkError == that.retryOnNetworkError &&
                Objects.equals(backoffStrategy, that.backoffStrategy) &&
                Objects.equals(statusCodeMatcher, that.statusCodeMatcher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAttempts, backoffStrategy, jitterEnabled, jitterFactor, statusCodeMatcher,
                retryOnNetworkError);
    }

    @Override
    public String toString() {
        return "RetryOption{" +
                "maxAttempts=" + maxAttempts +
                ", backoffStrategy=" + backoffStrategy +
                ", jitterEnabled=" + jitterEnabled +
                ", jitterFactor=" + jitterFactor +
                ", statusCodeMatcher=" + statusCodeMatcher +
                ", retryOnNetworkError=" + retryOnNetworkError +
                '}';
    }
}
