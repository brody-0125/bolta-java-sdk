package io.bolta.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bolta.BoltaClient;
import io.bolta.exception.BoltaException;
import io.bolta.http.BoltaHttpHeader;
import io.bolta.http.HttpMethod;
import io.bolta.http.HttpRequest;
import io.bolta.model.ContractTerminationRequest;
import io.bolta.model.IssuanceKey;
import io.bolta.model.SupplyCostChangeRequest;
import io.bolta.model.TaxInvoice;
import io.bolta.model.TaxInvoiceIssuanceRequestOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Resource for e-tax invoice operations.
 * <p>
 * Supports three types of issuance:
 * <ul>
 * <li>Standard issuance - issued by the supplier</li>
 * <li>Reverse issuance - requested by the supplied party</li>
 * <li>Amendment - to correct previous invoices</li>
 * </ul>
 * <p>
 * Amendment types:
 * <ul>
 * <li>Cancellation of Contract</li>
 * <li>Change in Supply Cost</li>
 * <li>Double Issuance by Mistake</li>
 * <li>Correction of Error in Entries</li>
 * </ul>
 * <p>
 * 전자세금계산서 작업을 위한 리소스입니다.
 * <p>
 * 세 가지 발행 유형을 지원합니다:
 * <ul>
 * <li>정발행 - 공급자가 발행</li>
 * <li>역발행 - 공급받는자가 요청</li>
 * <li>수정발행 - 이전 세금계산서 수정</li>
 * </ul>
 * <p>
 * 수정발행 유형:
 * <ul>
 * <li>계약의 해제</li>
 * <li>공급가액 변동</li>
 * <li>착오로 이중 발급</li>
 * <li>착오로 기재사항을 잘못 적은 경우</li>
 * </ul>
 */
public final class TaxInvoiceResource {
    private static final Logger logger = LoggerFactory.getLogger(TaxInvoiceResource.class);
    private static final String BASE_PATH = "/v1/taxInvoices";

    private final BoltaClient client;

    public TaxInvoiceResource(BoltaClient client) {
        this.client = client;
    }

    /**
     * Issues an e-tax invoice synchronously.
     * <p>
     * 전자세금계산서를 동기적으로 정발행합니다 (전자세금계산서 정발행).
     *
     * @param invoice the tax invoice to issue
     * @return the issuance key for the issued invoice
     * @throws BoltaException if the issuance fails
     */
    public IssuanceKey issue(TaxInvoice invoice) {
        return issue(invoice, null);
    }

    /**
     * Issues an e-tax invoice synchronously with options.
     * <p>
     * Use TaxInvoiceRequestOptions to specify a customer key (Customer-Key header)
     * for
     * multi-customer platform scenarios.
     * <p>
     * 옵션과 함께 전자세금계산서를 동기적으로 정발행합니다 (전자세금계산서 정발행).
     * <p>
     * TaxInvoiceRequestOptions를 사용하여 다중 고객 플랫폼 시나리오에서
     * Customer-Key 헤더를 지정할 수 있습니다.
     *
     * @param invoice the tax invoice to issue
     * @param options request options (e.g., customer key for platform scenarios)
     * @return the issuance key for the issued invoice
     * @throws BoltaException if the issuance fails
     */
    public IssuanceKey issue(TaxInvoice invoice, TaxInvoiceIssuanceRequestOptions options) {
        logger.info("Issuing tax invoice for date: {}", invoice.getDate());
        return executeIssue(invoice, options);
    }

    /**
     * Issues an e-tax invoice asynchronously.
     * <p>
     * 전자세금계산서를 비동기적으로 정발행합니다 (전자세금계산서 정발행).
     *
     * @param invoice the tax invoice to issue
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> issueAsync(TaxInvoice invoice) {
        return issueAsync(invoice, null);
    }

    /**
     * Issues an e-tax invoice asynchronously with options.
     * <p>
     * 옵션과 함께 전자세금계산서를 비동기적으로 정발행합니다 (전자세금계산서 정발행).
     *
     * @param invoice the tax invoice to issue
     * @param options request options (e.g., customer key for platform scenarios)
     * @return a CompletableFuture containing the issuance key
     */
    public CompletableFuture<IssuanceKey> issueAsync(TaxInvoice invoice,
            TaxInvoiceIssuanceRequestOptions options) {
        CompletableFuture<IssuanceKey> future = new CompletableFuture<>();
        try {
            HttpRequest request = buildIssueRequest(invoice, options);
            client.enqueueRequest(request, TaxInvoiceIssueResponse.class,
                    new CompletableFuture<TaxInvoiceIssueResponse>() {
                        @Override
                        public boolean complete(TaxInvoiceIssueResponse value) {
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

    private IssuanceKey executeIssue(TaxInvoice invoice, TaxInvoiceIssuanceRequestOptions options) {
        logger.info("Issuing tax invoice for date: {}", invoice.getDate());
        HttpRequest request = buildIssueRequest(invoice, options);
        TaxInvoiceIssueResponse response = client.execute(request, TaxInvoiceIssueResponse.class, options);
        logger.info("Successfully issued tax invoice with key: {}", response.issuanceKey.getValue());
        return response.issuanceKey;
    }

    private HttpRequest buildIssueRequest(TaxInvoice invoice, TaxInvoiceIssuanceRequestOptions options) {
        try {
            String json = client.getObjectMapper().writeValueAsString(invoice);

            String url = client.buildUrl(BASE_PATH + "/issue");
            HttpRequest.Builder builder = HttpRequest.builder()
                    .url(url)
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json);

            if (options != null) {
                if (options.getCustomerKey() != null) {
                    builder.header(BoltaHttpHeader.CUSTOMER_KEY, options.getCustomerKey());
                }
                if (options.getClientReferenceId() != null) {
                    builder.header(BoltaHttpHeader.BOLTA_CLIENT_REFERENCE_ID, options.getClientReferenceId());
                }
            }

            return builder.build();
        } catch (IOException ioException) {
            throw new BoltaException("Failed to serialize tax invoice", ioException);
        }
    }

    /**
     * Retrieves e-tax invoice details by issuance key.
     * <p>
     * 발급 키로 전자세금계산서 상세 정보를 조회합니다 (전자세금계산서 조회).
     *
     * @param issuanceKey the issuance key returned when the invoice was issued
     * @return the tax invoice details
     * @throws BoltaException if the retrieval fails
     */
    public TaxInvoice get(String issuanceKey) {
        logger.info("Retrieving tax invoice for key: {}", issuanceKey);
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s", issuanceKey))
                .method(HttpMethod.GET)
                .build();

        TaxInvoice invoice = client.execute(request, TaxInvoice.class);
        logger.debug("Retrieved tax invoice for supplier: {}", invoice.getSupplier().getOrganizationName());
        return invoice;
    }

    /**
     * Issues a modified tax invoice due to contract termination.
     * <p>
     * 계약 해제로 인한 수정 세금계산서를 발행합니다 (계약의 해제).
     *
     * @param issuanceKey The issuance key of the original tax invoice (원본 세금계산서의
     *                    발급키)
     * @param request     The contract cancellation request details (계약 해제 요청 정보)
     * @return The issuance key of the modified tax invoice (수정 세금계산서의 발급키)
     */
    public IssuanceKey issueContractTermination(String issuanceKey, ContractTerminationRequest request) {
        return issueContractTermination(issuanceKey, request, null);
    }

    /**
     * Issues a modified tax invoice due to contract cancellation with options.
     * <p>
     * 옵션과 함께 계약 해제로 인한 수정 세금계산서를 발행합니다 (계약의 해제).
     *
     * @param issuanceKey The issuance key of the original tax invoice (원본 세금계산서의
     *                    발급키)
     * @param request     The contract cancellation request details (계약 해제 요청 정보)
     * @param options     Additional request options (추가 요청 옵션)
     * @return The issuance key of the modified tax invoice (수정 세금계산서의 발급키)
     */
    public IssuanceKey issueContractTermination(String issuanceKey, ContractTerminationRequest request,
            TaxInvoiceIssuanceRequestOptions options) {
        if (issuanceKey == null || issuanceKey.isEmpty()) {
            throw new IllegalArgumentException("issuanceKey is required");
        }
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }

        try {
            String json = client.getObjectMapper().writeValueAsString(request);

            HttpRequest.Builder requestBuilder = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH + "/%s/amend/termination", issuanceKey))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json);

            if (options != null) {
                if (options.getCustomerKey() != null) {
                    requestBuilder.header(BoltaHttpHeader.CUSTOMER_KEY, options.getCustomerKey());
                }
                if (options.getClientReferenceId() != null) {
                    requestBuilder.header(BoltaHttpHeader.BOLTA_CLIENT_REFERENCE_ID, options.getClientReferenceId());
                }
            }

            TaxInvoiceIssueResponse response = client.execute(requestBuilder.build(), TaxInvoiceIssueResponse.class,
                    options);
            return response.issuanceKey;
        } catch (IOException ioException) {
            throw new BoltaException("Failed to serialize contract cancellation request", ioException);
        }
    }

    /**
     * Issues a modified tax invoice due to supply cost change.
     * <p>
     * 공급가액 변동으로 인한 수정 세금계산서를 발행합니다 (공급가액 변동).
     *
     * @param issuanceKey The issuance key of the original tax invoice (원본 세금계산서의
     *                    발급키)
     * @param request     The supply cost change request details (공급가액 변동 요청 정보)
     * @return The issuance key of the modified tax invoice (수정 세금계산서의 발급키)
     */
    public IssuanceKey issueAmendSupplyCost(String issuanceKey, SupplyCostChangeRequest request) {
        return issueAmendSupplyCostChange(issuanceKey, request, null);
    }

    /**
     * Issues a modified tax invoice due to supply cost change with options.
     * <p>
     * 옵션과 함께 공급가액 변동으로 인한 수정 세금계산서를 발행합니다 (공급가액 변동).
     *
     * @param issuanceKey The issuance key of the original tax invoice (원본 세금계산서의
     *                    발급키)
     * @param request     The supply cost change request details (공급가액 변동 요청 정보)
     * @param options     Additional request options (추가 요청 옵션)
     * @return The issuance key of the modified tax invoice (수정 세금계산서의 발급키)
     */
    public IssuanceKey issueAmendSupplyCostChange(String issuanceKey, SupplyCostChangeRequest request,
            TaxInvoiceIssuanceRequestOptions options) {
        if (issuanceKey == null || issuanceKey.isEmpty()) {
            throw new IllegalArgumentException("issuanceKey is required");
        }
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }

        try {
            String json = client.getObjectMapper().writeValueAsString(request);

            HttpRequest.Builder requestBuilder = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH + "/%s/amend/changeSupplyCost", issuanceKey))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json);

            if (options != null) {
                if (options.getCustomerKey() != null) {
                    requestBuilder.header(BoltaHttpHeader.CUSTOMER_KEY, options.getCustomerKey());
                }
                if (options.getClientReferenceId() != null) {
                    requestBuilder.header(BoltaHttpHeader.BOLTA_CLIENT_REFERENCE_ID, options.getClientReferenceId());
                }
            }

            TaxInvoiceIssueResponse response = client.execute(requestBuilder.build(), TaxInvoiceIssueResponse.class,
                    options);
            return response.issuanceKey;
        } catch (IOException ioException) {
            throw new BoltaException("Failed to serialize supply cost change request", ioException);
        }
    }

    /**
     * Retrieves e-tax invoice details asynchronously by issuance key.
     * <p>
     * 발급 키로 전자세금계산서 상세 정보를 비동기적으로 조회합니다 (전자세금계산서 조회).
     *
     * @param issuanceKey the issuance key returned when the invoice was issued
     * @return a CompletableFuture containing the tax invoice details
     */
    public CompletableFuture<TaxInvoice> getAsync(String issuanceKey) {
        CompletableFuture<TaxInvoice> future = new CompletableFuture<>();
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s", issuanceKey))
                .method(HttpMethod.GET)
                .build();

        client.enqueueRequest(request, TaxInvoice.class, future);
        return future;
    }

    private static class TaxInvoiceIssueResponse {
        final IssuanceKey issuanceKey;

        @JsonCreator
        TaxInvoiceIssueResponse(@JsonProperty("issuanceKey") IssuanceKey issuanceKey) {
            this.issuanceKey = issuanceKey;
        }
    }
}
