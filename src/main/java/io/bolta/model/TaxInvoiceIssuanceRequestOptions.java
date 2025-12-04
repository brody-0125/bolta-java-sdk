package io.bolta.model;

import java.util.Objects;

/**
 * Configuration options for Tax Invoice API requests.
 * Includes options specific to tax invoice operations, such as customer key and
 * client reference ID.
 * <p>
 * 세금계산서 API 요청을 위한 구성 옵션입니다.
 * 고객 키 및 클라이언트 참조 ID와 같은 세금계산서 작업에 특화된 옵션을 포함합니다.
 */
public final class TaxInvoiceIssuanceRequestOptions extends RequestOptions {
    /**
     * Customer key for multi-customer platform scenarios.
     * This is sent as the "Customer-Key" header.
     * <p>
     * 다중 고객 플랫폼 시나리오를 위한 고객 키입니다.
     * "Customer-Key" 헤더로 전송됩니다.
     */
    private final String customerKey;

    /**
     * Client reference ID for tracking requests.
     * This is sent as the "Bolta-Client-Reference-Id" header.
     * <p>
     * 요청 추적을 위한 클라이언트 참조 ID입니다.
     * "Bolta-Client-Reference-Id" 헤더로 전송됩니다.
     */
    private final String clientReferenceId;

    private TaxInvoiceIssuanceRequestOptions(Builder builder) {
        super(builder);
        this.customerKey = builder.customerKey;
        this.clientReferenceId = builder.clientReferenceId;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public String getClientReferenceId() {
        return clientReferenceId;
    }

    @Override
    public java.util.Map<String, String> getHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>(super.getHeaders());
        if (customerKey != null) {
            headers.put("Customer-Key", customerKey);
        }
        if (clientReferenceId != null) {
            headers.put("Bolta-Client-Reference-Id", clientReferenceId);
        }
        return java.util.Collections.unmodifiableMap(headers);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing TaxInvoiceIssuanceRequestOptions instances.
     * <p>
     * TaxInvoiceIssuanceRequestOptions 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder extends RequestOptions.Builder<Builder> {
        private String customerKey;
        private String clientReferenceId;

        /**
         * Sets the customer key.
         *
         * @param customerKey the customer key
         * @return the builder instance
         */
        public Builder customerKey(String customerKey) {
            this.customerKey = customerKey;
            return this;
        }

        /**
         * Sets the client reference ID.
         *
         * @param clientReferenceId the client reference ID
         * @return the builder instance
         */
        public Builder clientReferenceId(String clientReferenceId) {
            this.clientReferenceId = clientReferenceId;
            return this;
        }

        @Override
        public TaxInvoiceIssuanceRequestOptions build() {
            return new TaxInvoiceIssuanceRequestOptions(this);
        }
    }

    @Override
    public String toString() {
        return "TaxInvoiceIssuanceRequestOptions{" +
                "customerKey='" + customerKey + '\'' +
                ", clientReferenceId='" + clientReferenceId + '\'' +
                ", retryOption=" + getRetryOption() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TaxInvoiceIssuanceRequestOptions that = (TaxInvoiceIssuanceRequestOptions) o;
        return Objects.equals(customerKey, that.customerKey) &&
                Objects.equals(clientReferenceId, that.clientReferenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customerKey, clientReferenceId);
    }
}
