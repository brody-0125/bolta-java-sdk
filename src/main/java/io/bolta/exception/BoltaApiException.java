package io.bolta.exception;

/**
 * Exception thrown when the Bolta API returns an error response.
 * 볼타 API가 오류 응답을 반환할 때 발생하는 예외입니다.
 * <p>
 * This exception contains:
 * 이 예외는 다음을 포함합니다:
 * <ul>
 * <li>HTTP status code (HTTP 상태 코드)</li>
 * <li>Error response body for debugging (디버깅을 위한 오류 응답 본문)</li>
 * </ul>
 */
public final class BoltaApiException extends BoltaException {
    /**
     * HTTP status code from the API response
     * API 응답의 HTTP 상태 코드
     */
    private final int statusCode;

    /**
     * Response body containing error details
     * 오류 세부 정보를 포함하는 응답 본문
     */
    private final String responseBody;

    public BoltaApiException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
