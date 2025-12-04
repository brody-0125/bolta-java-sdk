package io.bolta.http;

/**
 * Represents an HTTP request.
 * <p>
 * HTTP 요청을 나타냅니다.
 */
public final class HttpRequest {
    private final String url;
    private final HttpMethod method;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(Builder builder) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(builder.headers.getHeaders());

        this.url = builder.url;
        this.method = builder.method;
        this.headers = headers;
        this.body = builder.body;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Builder newBuilder() {
        Builder builder = new Builder()
                .url(this.url)
                .method(this.method)
                .headers(this.headers)
                .body(this.body);
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private HttpMethod method;
        private HttpHeaders headers = new HttpHeaders();
        private String body;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers.addAll(headers.getHeaders());
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            if (url == null)
                throw new IllegalStateException("URL is required");
            if (method == null)
                throw new IllegalStateException("Method is required");
            return new HttpRequest(this);
        }
    }
}
