package io.bolta.exception;

/**
 * Base exception for all SDK errors.
 * SDK의 모든 오류에 대한 기본 예외입니다.
 * <p>
 * This exception is thrown for:
 * 다음의 경우에 이 예외가 발생합니다:
 * <ul>
 * <li>Network errors (네트워크 오류)</li>
 * <li>JSON serialization/deserialization errors (JSON 직렬화/역직렬화 오류)</li>
 * <li>General SDK errors (일반 SDK 오류)</li>
 * </ul>
 */
public class BoltaException extends RuntimeException {
    /**
     * Constructs a new BoltaException with the specified message.
     * <p>
     * 지정된 메시지로 새 BoltaException을 생성합니다.
     *
     * @param message the detail message
     */
    public BoltaException(String message) {
        super(message);
    }

    /**
     * Constructs a new BoltaException with the specified message and cause.
     * <p>
     * 지정된 메시지와 원인으로 새 BoltaException을 생성합니다.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public BoltaException(String message, Throwable cause) {
        super(message, cause);
    }
}
