package io.bolta.retry;

/**
 * Strategy for calculating delay between retry attempts.
 * 재시도 간 지연 시간을 계산하는 전략입니다.
 */
public interface BackoffStrategy {
    /**
     * Calculates the delay before the next retry attempt.
     * 다음 재시도 전 지연 시간을 계산합니다.
     *
     * @param attemptNumber the current attempt number (starting from 1)
     *                      현재 시도 횟수 (1부터 시작)
     * @return delay in milliseconds
     *         밀리초 단위의 지연 시간
     */
    long calculateDelay(int attemptNumber);
}
