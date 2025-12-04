package io.bolta.retry;

import java.util.Random;

/**
 * Random delay backoff strategy that returns a random delay within a specified
 * range.
 * 지정된 범위 내에서 무작위 지연 시간을 반환하는 랜덤 지연 백오프 전략입니다.
 */
public final class RandomBackoffStrategy implements BackoffStrategy {
    private final long minDelayMillis;
    private final long maxDelayMillis;
    private final Random random;

    public RandomBackoffStrategy(long minDelayMillis, long maxDelayMillis) {
        this(minDelayMillis, maxDelayMillis, new Random());
    }

    public RandomBackoffStrategy(long minDelayMillis, long maxDelayMillis, Random random) {
        if (minDelayMillis < 0) {
            throw new IllegalArgumentException("minDelayMillis must be non-negative");
        }
        if (maxDelayMillis < minDelayMillis) {
            throw new IllegalArgumentException("maxDelayMillis must be >= minDelayMillis");
        }
        this.minDelayMillis = minDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.random = random;
    }

    @Override
    public long calculateDelay(int attemptNumber) {
        if (minDelayMillis == maxDelayMillis) {
            return minDelayMillis;
        }
        return minDelayMillis + (long) (random.nextDouble() * (maxDelayMillis - minDelayMillis));
    }

    public long getMinDelayMillis() {
        return minDelayMillis;
    }

    public long getMaxDelayMillis() {
        return maxDelayMillis;
    }
}
