package io.bolta.retry;

import java.util.HashSet;
import java.util.Set;

/**
 * Matches any of the specified HTTP status codes.
 * 지정된 HTTP 상태 코드 중 하나와 일치하는 매처입니다.
 * <p>
 * For example, to match 408, 429, and 503: ListStatusCodeMatcher.of(408, 429,
 * 503)
 * 예: 408, 429, 503을 매칭하려면: ListStatusCodeMatcher.of(408, 429, 503)
 */
public final class ListStatusCodeMatcher implements StatusCodeMatcher {
    private final Set<Integer> statusCodes;

    public ListStatusCodeMatcher(Set<Integer> statusCodes) {
        if (statusCodes == null || statusCodes.isEmpty()) {
            throw new IllegalArgumentException("statusCodes must not be null or empty");
        }
        for (Integer code : statusCodes) {
            if (code < 100 || code >= 600) {
                throw new IllegalArgumentException("Invalid HTTP status code: " + code);
            }
        }
        this.statusCodes = new HashSet<>(statusCodes);
    }

    public ListStatusCodeMatcher(int... codes) {
        if (codes == null || codes.length == 0) {
            throw new IllegalArgumentException("codes must not be null or empty");
        }
        this.statusCodes = new HashSet<>();
        for (int code : codes) {
            if (code < 100 || code >= 600) {
                throw new IllegalArgumentException("Invalid HTTP status code: " + code);
            }
            statusCodes.add(code);
        }
    }

    @Override
    public boolean matches(int statusCode) {
        return statusCodes.contains(statusCode);
    }

    public static ListStatusCodeMatcher of(int... codes) {
        return new ListStatusCodeMatcher(codes);
    }

    public Set<Integer> getStatusCodes() {
        return new HashSet<>(statusCodes);
    }
}
