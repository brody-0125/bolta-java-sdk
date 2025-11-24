package io.bolta.http.impl;

import io.bolta.http.HttpClient;
import io.bolta.http.HttpRequest;
import io.bolta.http.HttpResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of HttpClient using OkHttp.
 * This client can optionally add an Authorization header when an API key is
 * provided.
 */
public final class DefaultHttpClient implements HttpClient {
    private final OkHttpClient client;

    /**
     * Creates an DefaultHttpClient.
     *
     * @param client the underlying OkHttpClient instance
     */
    public DefaultHttpClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        Request okHttpRequest = toOkHttpRequest(request);
        try (Response response = client.newCall(okHttpRequest).execute()) {
            return toHttpResponse(response);
        }
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        Request okHttpRequest = toOkHttpRequest(request);
        client.newCall(okHttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    future.complete(toHttpResponse(response));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });
        return future;
    }

    private Request toOkHttpRequest(HttpRequest request) {
        Request.Builder builder = new Request.Builder()
                .url(request.getUrl());

        if (!request.getHeaders().getHeaders().isEmpty()) {
            for (Map.Entry<String, String> header : request.getHeaders().getHeaders().entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        RequestBody body = null;
        if (request.getBody() != null) {
            body = RequestBody.create(request.getBody(), MediaType.get("application/json"));
        }

        switch (request.getMethod()) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(body != null ? body : RequestBody.create(new byte[0], null));
                break;
            case PUT:
                builder.put(body != null ? body : RequestBody.create(new byte[0], null));
                break;
            case DELETE:
                builder.delete(body != null ? body : RequestBody.create(new byte[0], null));
                break;
        }
        return builder.build();
    }

    private HttpResponse toHttpResponse(Response response) throws IOException {
        Map<String, String> headers = new HashMap<>();
        for (String name : response.headers().names()) {
            headers.put(name, response.header(name));
        }
        String body = response.body() != null ? response.body().string() : null;
        return new HttpResponse(response.code(), headers, body);
    }
}
