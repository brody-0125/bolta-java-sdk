package io.bolta.resource;

import io.bolta.BoltaClient;
import io.bolta.exception.BoltaException;
import io.bolta.http.BoltaHttpHeader;
import io.bolta.http.HttpMethod;
import io.bolta.http.HttpRequest;
import io.bolta.model.Customer;
import io.bolta.model.RequestOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Resource for managing customers in the Bolta system.
 * <p>
 * Provides operations for:
 * <ul>
 * <li>Creating customers</li>
 * <li>Retrieving customer information</li>
 * <li>Getting certificate registration URL</li>
 * <li>Unregistering customer certificates</li>
 * </ul>
 * <p>
 * 볼타 시스템의 고객 관리를 위한 리소스입니다.
 * <p>
 * 다음 작업을 제공합니다:
 * <ul>
 * <li>고객 생성</li>
 * <li>고객 조회</li>
 * <li>고객 공동인증서 등록 URL 조회</li>
 * <li>고객 공동인증서 등록해제</li>
 * </ul>
 */
public final class CustomerResource {
    private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);
    private static final String BASE_PATH = "/v1/customers";

    private final BoltaClient client;

    /**
     * Constructs a new CustomerResource with the specified client.
     * <p>
     * 지정된 클라이언트로 새 CustomerResource를 생성합니다.
     *
     * @param client the BoltaClient to use for API calls
     */
    public CustomerResource(BoltaClient client) {
        this.client = client;
    }

    /**
     * Creates a new customer.
     * <p>
     * After creating a customer, they can register their certificate
     * to issue and receive e-tax invoices.
     * <p>
     * 새로운 고객을 생성합니다 (고객 생성).
     * <p>
     * 고객 생성 후 공동인증서를 등록하여 전자세금계산서를 발행하고 받을 수 있습니다.
     *
     * @param customer the customer information to create
     * @throws BoltaException if the creation fails
     */
    public void create(Customer customer) {
        create(customer, null);
    }

    /**
     * Creates a new customer with options.
     * <p>
     * 옵션과 함께 새로운 고객을 생성합니다.
     *
     * @param customer the customer information to create
     * @param options  request options (retry configuration only)
     * @throws BoltaException if the creation fails
     */
    public void create(Customer customer, RequestOptions options) {
        logger.info("Creating customer with ID: {}", customer.getIdentificationNumber());
        try {
            String json = client.getObjectMapper().writeValueAsString(customer);

            HttpRequest request = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json)
                    .build();

            client.execute(request, Void.class, options);
            logger.debug("Successfully created customer: {}", customer.getIdentificationNumber());
        } catch (IOException ioException) {
            logger.error("Failed to serialize customer: {}", customer.getIdentificationNumber(), ioException);
            throw new BoltaException("Failed to serialize customer", ioException);
        }
    }

    /**
     * Creates a new customer asynchronously.
     * <p>
     * 비동기적으로 새로운 고객을 생성합니다.
     *
     * @param customer the customer information to create
     * @return a CompletableFuture that completes when the creation is finished
     */
    public CompletableFuture<Void> createAsync(Customer customer) {
        return createAsync(customer, null);
    }

    /**
     * Creates a new customer asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 새로운 고객을 생성합니다.
     *
     * @param customer the customer information to create
     * @param options  request options (retry configuration only)
     * @return a CompletableFuture that completes when the creation is finished
     */
    public CompletableFuture<Void> createAsync(Customer customer, RequestOptions options) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            String json = client.getObjectMapper().writeValueAsString(customer);

            HttpRequest request = HttpRequest.builder()
                    .url(client.buildUrl(BASE_PATH))
                    .method(HttpMethod.POST)
                    .header(BoltaHttpHeader.CONTENT_TYPE, BoltaHttpHeader.APPLICATION_JSON)
                    .body(json)
                    .build();

            client.enqueueRequest(request, Void.class, future);
        } catch (Exception exception) {
            future.completeExceptionally(exception);
        }
        return future;
    }

    /**
     * Retrieves customer information by business registration number.
     * <p>
     * 사업자등록번호로 고객 정보를 조회합니다 (고객 조회).
     *
     * @param identificationNumber the business registration number (사업자등록번호)
     * @return the customer information
     * @throws BoltaException if the retrieval fails
     */
    public Customer get(String identificationNumber) {
        return get(identificationNumber, null);
    }

    /**
     * Retrieves customer information by business registration number with options.
     * <p>
     * 옵션과 함께 사업자등록번호로 고객 정보를 조회합니다.
     *
     * @param identificationNumber the business registration number (사업자등록번호)
     * @param options              request options (retry configuration only)
     * @return the customer information
     * @throws BoltaException if the retrieval fails
     */
    public Customer get(String identificationNumber, RequestOptions options) {
        logger.info("Fetching customer with ID: {}", identificationNumber);
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s", identificationNumber))
                .method(HttpMethod.GET)
                .build();

        Customer customer = client.execute(request, Customer.class, options);
        logger.debug("Successfully fetched customer: {}", identificationNumber);
        return customer;
    }

    /**
     * Retrieves customer information asynchronously.
     * <p>
     * 비동기적으로 고객 정보를 조회합니다.
     *
     * @param identificationNumber the business registration number (사업자등록번호)
     * @return a CompletableFuture containing the customer information
     */
    public CompletableFuture<Customer> getAsync(String identificationNumber) {
        return getAsync(identificationNumber, null);
    }

    /**
     * Retrieves customer information asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 고객 정보를 조회합니다.
     *
     * @param identificationNumber the business registration number (사업자등록번호)
     * @param options              request options (retry configuration only)
     * @return a CompletableFuture containing the customer information
     */
    public CompletableFuture<Customer> getAsync(String identificationNumber, RequestOptions options) {
        CompletableFuture<Customer> future = new CompletableFuture<>();
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s", identificationNumber))
                .method(HttpMethod.GET)
                .build();

        client.enqueueRequest(request, Customer.class, future);
        return future;
    }

    /**
     * Retrieves the certificate registration URL for a customer.
     * <p>
     * 고객의 공동인증서 등록 URL을 조회합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @return the certificate registration URL
     * @throws BoltaException if the retrieval fails
     */
    public String getCertificateRegistrationUrl(String customerKey) {
        return getCertificateRegistrationUrl(customerKey, null);
    }

    /**
     * Retrieves the certificate registration URL for a customer with options.
     * <p>
     * 옵션과 함께 고객의 공동인증서 등록 URL을 조회합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @param options     request options (retry configuration only)
     * @return the certificate registration URL
     * @throws BoltaException if the retrieval fails
     */
    public String getCertificateRegistrationUrl(String customerKey, RequestOptions options) {
        logger.info("Fetching certificate registration URL for customer: {}", customerKey);
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/certificates/url", customerKey))
                .method(HttpMethod.GET)
                .build();

        CertificateUrlResponse response = client.execute(request, CertificateUrlResponse.class, options);
        logger.debug("Successfully fetched certificate URL for customer: {}", customerKey);
        return response.url;
    }

    /**
     * Retrieves the certificate registration URL asynchronously.
     * <p>
     * 비동기적으로 고객의 공동인증서 등록 URL을 조회합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @return a CompletableFuture containing the certificate registration URL
     */
    public CompletableFuture<String> getCertificateRegistrationUrlAsync(String customerKey) {
        return getCertificateRegistrationUrlAsync(customerKey, null);
    }

    /**
     * Retrieves the certificate registration URL asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 고객의 공동인증서 등록 URL을 조회합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @param options     request options (retry configuration only)
     * @return a CompletableFuture containing the certificate registration URL
     */
    public CompletableFuture<String> getCertificateRegistrationUrlAsync(String customerKey, RequestOptions options) {
        CompletableFuture<String> future = new CompletableFuture<>();
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/certificates/url", customerKey))
                .method(HttpMethod.GET)
                .build();

        client.enqueueRequest(request, CertificateUrlResponse.class,
                new CompletableFuture<CertificateUrlResponse>() {
                    @Override
                    public boolean complete(CertificateUrlResponse value) {
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
     * Unregisters customer certificate.
     * <p>
     * 고객의 공동인증서 등록을 해제합니다 (고객 공동인증서 등록해제).
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @throws BoltaException if the deletion fails
     */
    public void deleteCertificate(String customerKey) {
        deleteCertificate(customerKey, null);
    }

    /**
     * Unregisters customer certificate with options.
     * <p>
     * 옵션과 함께 고객의 공동인증서 등록을 해제합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @param options     request options (retry configuration only)
     * @throws BoltaException if the deletion fails
     */
    public void deleteCertificate(String customerKey, RequestOptions options) {
        logger.info("Deleting certificate for customer: {}", customerKey);
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/certificates", customerKey))
                .method(HttpMethod.DELETE)
                .build();

        client.execute(request, Void.class, options);
        logger.debug("Successfully deleted certificate for customer: {}", customerKey);
    }

    /**
     * Unregisters customer certificate asynchronously.
     * <p>
     * 비동기적으로 고객의 공동인증서 등록을 해제합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @return a CompletableFuture that completes when the deletion is finished
     */
    public CompletableFuture<Void> deleteCertificateAsync(String customerKey) {
        return deleteCertificateAsync(customerKey, null);
    }

    /**
     * Unregisters customer certificate asynchronously with options.
     * <p>
     * 옵션과 함께 비동기적으로 고객의 공동인증서 등록을 해제합니다.
     *
     * @param customerKey the customer key (고객 키, e.g., "customer_bf8paz")
     * @param options     request options (retry configuration only)
     * @return a CompletableFuture that completes when the deletion is finished
     */
    public CompletableFuture<Void> deleteCertificateAsync(String customerKey, RequestOptions options) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        HttpRequest request = HttpRequest.builder()
                .url(client.buildUrl(BASE_PATH + "/%s/certificates", customerKey))
                .method(HttpMethod.DELETE)
                .build();

        client.enqueueRequest(request, Void.class, future);
        return future;
    }

    private static class CertificateUrlResponse {
        final String url;

        @JsonCreator
        CertificateUrlResponse(@JsonProperty("url") String url) {
            this.url = url;
        }
    }
}
