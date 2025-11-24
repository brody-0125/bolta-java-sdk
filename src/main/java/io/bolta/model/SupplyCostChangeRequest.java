package io.bolta.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Request model for modifying a tax invoice due to supply cost change (공급가액
 * 변동).
 */
public final class SupplyCostChangeRequest {
    /**
     * Change date (yyyy-MM-dd)
     * 변동 사유 발생일
     */
    private final String date;

    /**
     * List of items with changed supply cost
     * 변동된 공급가액이 반영된 품목 목록
     */
    private final List<TaxInvoiceLineItem> items;

    private SupplyCostChangeRequest() {
        this.date = null;
        this.items = null;
    }

    private SupplyCostChangeRequest(String date, List<TaxInvoiceLineItem> items) {
        if (date == null)
            throw new NullPointerException("date is marked non-null but is null");
        if (items == null)
            throw new NullPointerException("items is marked non-null but is null");
        this.date = date;
        this.items = items;
    }

    private SupplyCostChangeRequest(Builder builder) {
        this.date = builder.date;
        this.items = builder.items;
    }

    public String getDate() {
        return date;
    }

    public List<TaxInvoiceLineItem> getTaxInvoiceLineItems() {
        return items == null ? null : Collections.unmodifiableList(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String date;
        private List<TaxInvoiceLineItem> items;

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder items(List<TaxInvoiceLineItem> items) {
            this.items = items;
            return this;
        }

        public SupplyCostChangeRequest build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            if (items == null)
                throw new NullPointerException("items is marked non-null but is null");
            return new SupplyCostChangeRequest(this);
        }
    }

    @Override
    public String toString() {
        return "SupplyCostChangeRequest{" +
                "date='" + date + '\'' +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SupplyCostChangeRequest that = (SupplyCostChangeRequest) o;
        return Objects.equals(date, that.date) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, items);
    }
}
