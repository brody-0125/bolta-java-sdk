package io.bolta.model;

import java.util.Objects;

/**
 * Request model for modifying a tax invoice due to contract termination.
 * Used when a contract is canceled and the original tax invoice needs to be corrected.
 * <p>
 * 계약 해제로 인한 세금계산서 수정 요청 모델입니다.
 * 계약이 취소되어 원본 세금계산서를 정정해야 할 때 사용됩니다.
 *
 * @see io.bolta.resource.TaxInvoiceResource#issueContractTermination(String, ContractTerminationRequest)
 */
public final class ContractTerminationRequest {
    /**
     * Cancellation date (yyyy-MM-dd)
     * 계약 해제일
     */
    private final String date;

    private ContractTerminationRequest() {
        this.date = null;
    }

    private ContractTerminationRequest(String date) {
        if (date == null)
            throw new NullPointerException("date is marked non-null but is null");
        this.date = date;
    }

    private ContractTerminationRequest(Builder builder) {
        this.date = builder.date;
    }

    /**
     * Returns the contract termination date.
     * <p>
     * 계약 해제일을 반환합니다.
     *
     * @return the termination date in yyyy-MM-dd format
     */
    public String getDate() {
        return date;
    }

    /**
     * Creates a new builder for ContractTerminationRequest.
     * <p>
     * ContractTerminationRequest를 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing ContractTerminationRequest instances.
     * <p>
     * ContractTerminationRequest 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private String date;

        /**
         * Sets the contract termination date.
         * <p>
         * 계약 해제일을 설정합니다.
         *
         * @param date the termination date in yyyy-MM-dd format
         * @return this builder
         */
        public Builder date(String date) {
            this.date = date;
            return this;
        }

        /**
         * Builds a new ContractTerminationRequest instance.
         * <p>
         * 새 ContractTerminationRequest 인스턴스를 생성합니다.
         *
         * @return the configured ContractTerminationRequest
         * @throws NullPointerException if date is null
         */
        public ContractTerminationRequest build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            return new ContractTerminationRequest(this);
        }
    }

    @Override
    public String toString() {
        return "ContractTerminationRequest{" +
                "date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ContractTerminationRequest that = (ContractTerminationRequest) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
