package io.bolta.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Request model for reverse issuance (역발행) of a tax invoice.
 * <p>
 * In reverse issuance, the supplied party requests the issuance, and the
 * supplier approves it.
 * 역발행에서는 공급받는자가 발행을 요청하고 공급자가 승인합니다.
 */
public final class TaxInvoiceIssuanceRequest {
    /**
     * Write date (yyyy-MM-dd)
     * 작성일자
     */
    private final String date;

    /**
     * Issuance purpose
     * 발행 목적
     */
    private final IssuancePurpose purpose;

    /**
     * Supplier information
     * 공급자 정보
     */
    private final Supplier supplier;

    /**
     * Supplied party information
     * 공급받는자 정보
     */
    private final Supplied supplied;

    /**
     * List of items
     * 품목 목록
     */
    private final List<TaxInvoiceLineItem> items;

    /**
     * Description
     * 비고
     */
    private final String description;

    private TaxInvoiceIssuanceRequest() {
        this.date = null;
        this.purpose = null;
        this.supplier = null;
        this.supplied = null;
        this.items = null;
        this.description = null;
    }

    private TaxInvoiceIssuanceRequest(String date, IssuancePurpose purpose, Supplier supplier,
            Supplied supplied, List<TaxInvoiceLineItem> items, String description) {
        if (date == null)
            throw new NullPointerException("date is marked non-null but is null");
        if (purpose == null)
            throw new NullPointerException("purpose is marked non-null but is null");
        if (supplier == null)
            throw new NullPointerException("supplier is marked non-null but is null");
        if (supplied == null)
            throw new NullPointerException("supplied is marked non-null but is null");
        if (items == null)
            throw new NullPointerException("items is marked non-null but is null");
        this.date = date;
        this.purpose = purpose;
        this.supplier = supplier;
        this.supplied = supplied;
        this.items = items;
        this.description = description;
    }

    private TaxInvoiceIssuanceRequest(Builder builder) {
        this.date = builder.date;
        this.purpose = builder.purpose;
        this.supplier = builder.supplier;
        this.supplied = builder.supplied;
        this.items = builder.items;
        this.description = builder.description;
    }

    public String getDate() {
        return date;
    }

    public IssuancePurpose getPurpose() {
        return purpose;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Supplied getSupplied() {
        return supplied;
    }

    public List<TaxInvoiceLineItem> getTaxInvoiceLineItems() {
        return items == null ? null : Collections.unmodifiableList(items);
    }

    public String getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String date;
        private IssuancePurpose purpose;
        private Supplier supplier;
        private Supplied supplied;
        private List<TaxInvoiceLineItem> items;
        private String description;

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder purpose(IssuancePurpose purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder supplier(Supplier supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder supplied(Supplied supplied) {
            this.supplied = supplied;
            return this;
        }

        public Builder items(List<TaxInvoiceLineItem> items) {
            this.items = items;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public TaxInvoiceIssuanceRequest build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            if (purpose == null)
                throw new NullPointerException("purpose is marked non-null but is null");
            if (supplier == null)
                throw new NullPointerException("supplier is marked non-null but is null");
            if (supplied == null)
                throw new NullPointerException("supplied is marked non-null but is null");
            if (items == null)
                throw new NullPointerException("items is marked non-null but is null");
            return new TaxInvoiceIssuanceRequest(this);
        }
    }

    @Override
    public String toString() {
        return "TaxInvoiceIssuanceRequest{" +
                "date='" + date + '\'' +
                ", purpose=" + purpose +
                ", supplier=" + supplier +
                ", supplied=" + supplied +
                ", items=" + items +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaxInvoiceIssuanceRequest that = (TaxInvoiceIssuanceRequest) o;
        return Objects.equals(date, that.date) &&
                purpose == that.purpose &&
                Objects.equals(supplier, that.supplier) &&
                Objects.equals(supplied, that.supplied) &&
                Objects.equals(items, that.items) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, purpose, supplier, supplied, items, description);
    }
}
