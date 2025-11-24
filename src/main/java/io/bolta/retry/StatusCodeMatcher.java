package io.bolta.retry;

/**
 * Matcher for determining if an HTTP status code should trigger a retry.
 * HTTP 상태 코드가 재시도를 트리거해야 하는지 판단하는 매처입니다.
 */
public interface StatusCodeMatcher {
    /**
     * Checks if the given status code should trigger a retry.
     * 주어진 상태 코드가 재시도를 트리거해야 하는지 확인합니다.
     *
     * @param statusCode the HTTP status code to check
     *                   확인할 HTTP 상태 코드
     * @return true if the status code should trigger a retry
     *         상태 코드가 재시도를 트리거해야 하면 true
     */
    boolean matches(int statusCode);
}
