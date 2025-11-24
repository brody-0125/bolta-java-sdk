package io.bolta.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bolta.BoltaClient;
import io.bolta.exception.BoltaException;
import io.bolta.http.BoltaHttpHeader;
import io.bolta.http.HttpMethod;
import io.bolta.http.HttpRequest;
import io.bolta.model.IssuanceKey;
import io.bolta.model.TaxInvoiceIssuanceRequest;
import io.bolta.model.TaxInvoiceIssuanceRequestOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Resource for reverse issuance operations.
 * <p>
 * Reverse issuance involves the supplied party requesting the issuance, and the
 * supplier approving it.
 * <p>
 * 역발행 작업을 위한 리소스입니다 (역발행).
 * <p>
 * 역발행은 공급받는자가 발행을 요청하고 공급자가 승인하는 방식입니다.
 */
public final class TaxInvoiceIssuanceRequestResource {
    private static final Logger logger = LoggerFactory.getLogger(TaxInvoiceIssuanceRequestResource.class);
    private static final String BASE_PATH = "/v1/taxInvoices";

    private final BoltaClient client;

    public TaxInvoiceIssuanceRequestResource(BoltaClient client) {
        this.client = client;
    }

    /**
     * Requests a reverse issuance of a tax invoice.
     * <p>
     * 역발행을 요청합니다.
     *
     * @param request The reverse issuance request details (역발행 요청 정보)
     * @return The issuance key of the requested tax invoice (요청된 세금계산서의 발급키)
     */
    public IssuanceKey request(TaxInvoiceIssuanceRequest request) {
        return request(request, null);
    }

    /**
     * Requests a reverse issuance of a tax invoice with options.
     * <p>
     * 옵션과 함께 역발행을 요청합니다.
     *
     * @param request The reverse issuance request details (역발행 요청 정보)
     * @param options Additional request options (retry configuration only)
     * @return The issuance key of the requested tax invoice (요청된 세금계산서의 발급키)
     */
    public IssuanceKey request(TaxInvoiceIssuanceRequest request, TaxInvoiceIssuanceRequestOptions options) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }

        logger.info("Requesting reverse issuance{}", options != null ? " with options" : "");
        logger.debug("Reverse issuance request details: supplier={}, supplied={}, purpose={}",
                request.getSupplier().getOrganizationName(),
                request.getSupplied().getOrganizationName(),
                request.getPurpose());

        try {
            String json = client.getObjectMapper().writeValueAsString(request);

            HttpRequest.Builder requestBuilder = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH + "/issueRequest"))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json);

            IssueResponse response = client.execute(requestBuilder.build(), IssueResponse.class, options);
            logger.info("Successfully requested reverse issuance with key: {}", response.issuanceKey.getValue());
            return response.issuanceKey;
        } catch (IOException ioException) {
            logger.error("Failed to serialize reverse issuance request", ioException);
            throw new BoltaException("Failed to serialize reverse issuance request", ioException);
        }
    }

    /**
     * Requests a reverse issuance of a tax invoice asynchronously.
     * <p>
     * 비동기적으로 역발행을 요청합니다.
     *
     * @param request The reverse issuance request details (역발행 요청 정보)
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> requestAsync(TaxInvoiceIssuanceRequest request) {
        return requestAsync(request, null);
    }

    /**
     * Requests a reverse issuance of a tax invoice asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 역발행을 요청합니다.
     *
     * @param request The reverse issuance request details (역발행 요청 정보)
     * @param options Additional request options (retry configuration only)
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> requestAsync(TaxInvoiceIssuanceRequest request, TaxInvoiceIssuanceRequestOptions options) {
        CompletableFuture<IssuanceKey> future = new CompletableFuture<>();
        try {
            String json = client.getObjectMapper().writeValueAsString(request);

            HttpRequest.Builder requestBuilder = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH + "/issueRequest"))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json);

            client.enqueueRequest(requestBuilder.build(), IssueResponse.class,
                    new CompletableFuture<IssueResponse>() {
                        @Override
                        public boolean complete(IssueResponse value) {
                            return future.complete(value.issuanceKey);
                        }

                        @Override
                        public boolean completeExceptionally(Throwable ex) {
                            return future.completeExceptionally(ex);
                        }
                    });
        } catch (Exception exception) {
            future.completeExceptionally(exception);
        }
        return future;
    }

    /**
     * Retrieves the approval URL for a reverse issuance request.
     * <p>
     * 역발행 요청에 대한 승인 URL을 조회합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @return The approval URL (승인 URL)
     */
    public String getGrantUrl(String issuanceKey) {
        return getGrantUrl(issuanceKey, null);
    }

    /**
     * Retrieves the approval URL for a reverse issuance request with options.
     * <p>
     * 옵션과 함께 역발행 요청에 대한 승인 URL을 조회합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @param options     Additional request options (retry configuration only)
     * @return The approval URL (승인 URL)
     */
    public String getGrantUrl(String issuanceKey, TaxInvoiceIssuanceRequestOptions options) {
        if (issuanceKey == null || issuanceKey.isEmpty()) {
            throw new IllegalArgumentException("issuanceKey is required");
        }

        logger.info("Fetching grant URL for issuance key: {}", issuanceKey);

        HttpRequest.Builder requestBuilder = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/issueRequest/grant", issuanceKey))
                .method(HttpMethod.GET);

        GrantUrlResponse response = client.execute(requestBuilder.build(), GrantUrlResponse.class, options);
        logger.debug("Successfully fetched grant URL for key: {}", issuanceKey);
        return response.url;
    }

    /**
     * Retrieves the approval URL for a reverse issuance request asynchronously.
     * <p>
     * 비동기적으로 역발행 요청에 대한 승인 URL을 조회합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @return a CompletableFuture containing the approval URL
     */
    public CompletableFuture<String> getGrantUrlAsync(String issuanceKey) {
        return getGrantUrlAsync(issuanceKey, null);
    }

    /**
     * Retrieves the approval URL for a reverse issuance request asynchronously with
     * options.
     * <p>
     * 옵션과 함께 비동기적으로 역발행 요청에 대한 승인 URL을 조회합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @param options     Additional request options (retry configuration only)
     * @return a CompletableFuture containing the approval URL
     */
    public CompletableFuture<String> getGrantUrlAsync(String issuanceKey,
            TaxInvoiceIssuanceRequestOptions options) {
        CompletableFuture<String> future = new CompletableFuture<>();
        HttpRequest.Builder requestBuilder = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/issueRequest/grant", issuanceKey))
                .method(HttpMethod.GET);

        client.enqueueRequest(requestBuilder.build(), GrantUrlResponse.class,
                new CompletableFuture<GrantUrlResponse>() {
                    @Override
                    public boolean complete(GrantUrlResponse value) {
                        return future.complete(value.url);
                    }

                    @Override
                    public boolean completeExceptionally(Throwable ex) {
                        return future.completeExceptionally(ex);
                    }
                });
        return future;
    }

    /**
     * Cancels a reverse issuance request.
     * <p>
     * 역발행 요청을 취소합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @return The issuance key of the cancelled request (취소된 요청의 발급키)
     */
    public IssuanceKey cancel(String issuanceKey) {
        return cancel(issuanceKey, null);
    }

    /**
     * Cancels a reverse issuance request with options.
     * <p>
     * 옵션과 함께 역발행 요청을 취소합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @param options     Additional request options (retry configuration only)
     * @return The issuance key of the cancelled request (취소된 요청의 발급키)
     */
    public IssuanceKey cancel(String issuanceKey, TaxInvoiceIssuanceRequestOptions options) {
        if (issuanceKey == null || issuanceKey.isEmpty()) {
            throw new IllegalArgumentException("issuanceKey is required");
        }

        logger.info("Cancelling reverse issuance request: {}", issuanceKey);

        HttpRequest.Builder requestBuilder = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/issueRequest/cancel", issuanceKey))
                .method(HttpMethod.PUT)
                .body("");

        IssueResponse response = client.execute(requestBuilder.build(), IssueResponse.class, options);
        logger.info("Successfully cancelled reverse issuance: {}", response.issuanceKey.getValue());
        return response.issuanceKey;
    }

    /**
     * Cancels a reverse issuance request asynchronously.
     * <p>
     * 비동기적으로 역발행 요청을 취소합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> cancelAsync(String issuanceKey) {
        return cancelAsync(issuanceKey, null);
    }

    /**
     * Cancels a reverse issuance request asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 역발행 요청을 취소합니다.
     *
     * @param issuanceKey The issuance key of the reverse issuance request (역발행 요청의
     *                    발급키)
     * @param options     Additional request options (retry configuration only)
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> cancelAsync(String issuanceKey,
            TaxInvoiceIssuanceRequestOptions options) {
        CompletableFuture<IssuanceKey> future = new CompletableFuture<>();
        HttpRequest.Builder requestBuilder = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/issueRequest/cancel", issuanceKey))
                .method(HttpMethod.PUT)
                .body(""); // Empty body for POST

        client.enqueueRequest(requestBuilder.build(), IssueResponse.class,
                new CompletableFuture<IssueResponse>() {
                    @Override
                    public boolean complete(IssueResponse value) {
                        return future.complete(value.issuanceKey);
                    }

                    @Override
                    public boolean completeExceptionally(Throwable ex) {
                        return future.completeExceptionally(ex);
                    }
                });
        return future;
    }

    private static class IssueResponse {
        final IssuanceKey issuanceKey;

        @JsonCreator
        IssueResponse(@JsonProperty("issuanceKey") IssuanceKey issuanceKey) {
            this.issuanceKey = issuanceKey;
        }
    }

    private static class GrantUrlResponse {
        final String url;

        @JsonCreator
        GrantUrlResponse(@JsonProperty("url") String url) {
            this.url = url;
        }
    }
}
