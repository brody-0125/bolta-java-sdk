package io.bolta.retry;

/**
 * Fixed delay backoff strategy that always returns the same delay.
 * 항상 동일한 지연 시간을 반환하는 고정 지연 백오프 전략입니다.
 */
public final class FixedBackoffStrategy implements BackoffStrategy {
    private final long delayMillis;

    public FixedBackoffStrategy(long delayMillis) {
        if (delayMillis < 0) {
            throw new IllegalArgumentException("delayMillis must be non-negative");
        }
        this.delayMillis = delayMillis;
    }

    @Override
    public long calculateDelay(int attemptNumber) {
        return delayMillis;
    }

    public long getDelayMillis() {
        return delayMillis;
    }
}
