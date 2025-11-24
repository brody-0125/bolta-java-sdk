package io.bolta.retry;

/**
 * Matches HTTP status codes within a specified range (inclusive).
 * 지정된 범위 내의 HTTP 상태 코드와 일치하는 매처입니다 (범위 포함).
 * <p>
 * For example, to match all 5xx errors: new RangeStatusCodeMatcher(500, 599)
 * 예: 모든 5xx 에러를 매칭하려면: new RangeStatusCodeMatcher(500, 599)
 */
public final class RangeStatusCodeMatcher implements StatusCodeMatcher {
    private final int min;
    private final int max;

    public RangeStatusCodeMatcher(int min, int max) {
        if (min < 100 || min >= 600) {
            throw new IllegalArgumentException("Invalid HTTP status code min: " + min);
        }
        if (max < 100 || max >= 600) {
            throw new IllegalArgumentException("Invalid HTTP status code max: " + max);
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(int statusCode) {
        return statusCode >= min && statusCode <= max;
    }

    public static RangeStatusCodeMatcher of(int min, int max) {
        return new RangeStatusCodeMatcher(min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
