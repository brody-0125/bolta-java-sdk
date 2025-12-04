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

    /**
     * Constructs a new BoltaApiException with the specified details.
     * <p>
     * 지정된 세부 정보로 새 BoltaApiException을 생성합니다.
     *
     * @param statusCode   the HTTP status code from the API response
     * @param message      the error message
     * @param responseBody the response body containing error details
     */
    public BoltaApiException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Returns the HTTP status code from the API response.
     * <p>
     * API 응답의 HTTP 상태 코드를 반환합니다.
     *
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the response body containing error details.
     * <p>
     * 오류 세부 정보를 포함하는 응답 본문을 반환합니다.
     *
     * @return the response body
     */
    public String getResponseBody() {
        return responseBody;
    }
}
