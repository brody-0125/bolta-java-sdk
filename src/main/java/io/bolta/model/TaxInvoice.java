package io.bolta.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an e-tax invoice in the Korean tax system.
 * <p>
 * E-tax invoices can be issued in three ways:
 * <ul>
 * <li>Standard issuance - issued by the supplier to the supplied party</li>
 * <li>Reverse issuance - requested by the supplied party, approved by the
 * supplier</li>
 * <li>Amendment - issued to correct or modify a previous invoice</li>
 * </ul>
 * <p>
 * Amendment types include:
 * <ul>
 * <li>Cancellation of Contract</li>
 * <li>Change in Supply Cost</li>
 * <li>Double Issuance by Mistake</li>
 * <li>Correction of Error in Entries</li>
 * </ul>
 * <p>
 * 한국 세금 시스템의 전자세금계산서를 나타냅니다.
 * <p>
 * 전자세금계산서는 세 가지 방식으로 발행할 수 있습니다:
 * <ul>
 * <li>정발행 - 공급자가 공급받는자에게 발행</li>
 * <li>역발행 - 공급받는자가 요청하고 공급자가 승인</li>
 * <li>수정발행 - 이전 세금계산서를 수정하거나 정정하기 위해 발행</li>
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
public final class TaxInvoice {
    /**
     * Write date of the e-tax invoice in yyyy-MM-dd format
     * <p>
     * 전자세금계산서 작성일자 (yyyy-MM-dd 형식)
     */
    private final String date; // yyyy-MM-dd

    /**
     * Issuance purpose - receipt or claim
     * <p>
     * 발행 목적 - 영수 또는 청구
     */
    private final IssuancePurpose purpose;

    /**
     * e-Tax Invoice Supplier information
     * <p>
     * 공급자 정보
     */
    private final Supplier supplier;

    /**
     * e-Tax Invoice Supplied Party information
     * <p>
     * 공급받는자 정보
     */
    private final Supplied supplied;

    /**
     * List of items on the invoice
     * <p>
     * 세금계산서의 품목 목록
     */
    private final List<TaxInvoiceLineItem> items;

    /**
     * Additional description or notes for the invoice
     * <p>
     * 세금계산서의 추가 설명 또는 비고
     */
    private final String description;

    private TaxInvoice() {
        this.date = null;
        this.purpose = null;
        this.supplier = null;
        this.supplied = null;
        this.items = null;
        this.description = null;
    }

    private TaxInvoice(String date, IssuancePurpose purpose, Supplier supplier, Supplied supplied,
            List<TaxInvoiceLineItem> items,
            String description) {
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

    private TaxInvoice(Builder builder) {
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

    public List<TaxInvoiceLineItem> getItems() {
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

        public TaxInvoice build() {
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
            return new TaxInvoice(this);
        }
    }

    @Override
    public String toString() {
        return "TaxInvoice{" +
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
        TaxInvoice that = (TaxInvoice) o;
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
