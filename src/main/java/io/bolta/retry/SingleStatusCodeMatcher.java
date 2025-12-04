package io.bolta.retry;

/**
 * Matches a single specific HTTP status code.
 * 단일 특정 HTTP 상태 코드와 일치하는 매처입니다.
 */
public final class SingleStatusCodeMatcher implements StatusCodeMatcher {
    private final int targetCode;

    public SingleStatusCodeMatcher(int targetCode) {
        if (targetCode < 100 || targetCode >= 600) {
            throw new IllegalArgumentException("Invalid HTTP status code: " + targetCode);
        }
        this.targetCode = targetCode;
    }

    @Override
    public boolean matches(int statusCode) {
        return statusCode == targetCode;
    }

    public static SingleStatusCodeMatcher of(int code) {
        return new SingleStatusCodeMatcher(code);
    }

    public int getTargetCode() {
        return targetCode;
    }
}
