package io.bolta.retry;

/**
 * Exponential backoff strategy that increases delay exponentially with each
 * attempt.
 * 시도할 때마다 지연 시간이 지수적으로 증가하는 백오프 전략입니다.
 * <p>
 * The delay is calculated as: baseDelay * (multiplier ^ (attemptNumber - 1))
 * 지연 시간은 다음과 같이 계산됩니다: baseDelay * (multiplier ^ (attemptNumber - 1))
 */
public final class ExponentialBackoffStrategy implements BackoffStrategy {
    private final long baseDelayMillis;
    private final double multiplier;
    private final long maxDelayMillis;

    public ExponentialBackoffStrategy(long baseDelayMillis, double multiplier, long maxDelayMillis) {
        if (baseDelayMillis < 0) {
            throw new IllegalArgumentException("baseDelayMillis must be non-negative");
        }
        if (multiplier <= 0) {
            throw new IllegalArgumentException("multiplier must be positive");
        }
        if (maxDelayMillis < baseDelayMillis) {
            throw new IllegalArgumentException("maxDelayMillis must be >= baseDelayMillis");
        }
        this.baseDelayMillis = baseDelayMillis;
        this.multiplier = multiplier;
        this.maxDelayMillis = maxDelayMillis;
    }

    @Override
    public long calculateDelay(int attemptNumber) {
        if (attemptNumber <= 1) {
            return baseDelayMillis;
        }
        double delay = baseDelayMillis * Math.pow(multiplier, attemptNumber - 1);
        return Math.min((long) delay, maxDelayMillis);
    }

    public long getBaseDelayMillis() {
        return baseDelayMillis;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public long getMaxDelayMillis() {
        return maxDelayMillis;
    }
}
