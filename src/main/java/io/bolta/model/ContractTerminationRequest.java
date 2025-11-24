package io.bolta.model;

import java.util.Objects;

/**
 * Request model for modifying a tax invoice due to contract cancellation (계약의
 * 해제).
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

    public String getDate() {
        return date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String date;

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public ContractTerminationRequest build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            return new ContractTerminationRequest(this);
        }
    }

    @Override
    public String toString() {
        return "ContractCancellationRequest{" +
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
