package io.bolta;

import io.bolta.resource.CustomerResource;
import io.bolta.resource.TaxInvoiceIssuanceRequestResource;
import io.bolta.resource.TaxInvoiceResource;

/**
 * High-level entry point for the Bolta SDK.
 * 볼타 SDK의 고수준 진입점입니다.
 * <p>
 * This class provides convenient access to all Bolta API resources including:
 * 이 클래스는 다음을 포함한 모든 볼타 API 리소스에 대한 편리한 액세스를 제공합니다:
 * <ul>
 * <li>E-tax invoice (전자세금계산서) operations</li>
 * <li>Customer (고객) management</li>
 * </ul>
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * BoltaApp app = BoltaApp.builder()
 *         .apiKey("your_api_key")
 *         .build();
 * 
 * IssuanceKey key = app.taxInvoices().issue(invoice);
 * }</pre>
 */
public final class BoltaApp {
    private final BoltaClient client;

    private BoltaApp(Builder builder) {
        this.client = builder.client;
    }

    /**
     * Returns the tax invoice resource for e-tax invoice (전자세금계산서) operations.
     * <p>
     * 전자세금계산서 작업을 위한 세금계산서 리소스를 반환합니다.
     *
     * @return the TaxInvoiceResource instance
     */
    public TaxInvoiceResource taxInvoices() {
        return new TaxInvoiceResource(client);
    }

    /**
     * Returns a resource for tax invoice reverse issuance operations.
     * <p>
     * 세금계산서 역발행 작업을 위한 리소스를 반환합니다.
     *
     * @return TaxInvoiceIssuanceRequestResource instance
     */
    public TaxInvoiceIssuanceRequestResource taxInvoiceIssuanceRequests() {
        return new TaxInvoiceIssuanceRequestResource(client);
    }

    /**
     * Returns the customer resource for customer (고객) management operations.
     * <p>
     * 고객 관리 작업을 위한 고객 리소스를 반환합니다.
     *
     * @return the CustomerResource instance
     */
    public CustomerResource customers() {
        return new CustomerResource(client);
    }

    public static class Builder {
        private BoltaClient client;

        public Builder client(BoltaClient client) {
            this.client = client;
            return this;
        }

        public BoltaApp build() {
            if (client == null) {
                throw new IllegalArgumentException("BoltaClient is required");
            }

            return new BoltaApp(this);
        }
    }

    /**
     * Creates a new builder for constructing a BoltaApp instance.
     * <p>
     * BoltaApp 인스턴스를 생성하기 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
