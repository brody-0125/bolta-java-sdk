package io.bolta.model;

import java.util.Objects;

/**
 * Represents an item (품목) in an e-tax invoice.
 * 전자세금계산서의 품목을 나타냅니다.
 */
public final class TaxInvoiceLineItem {
    /**
     * Item supply date in yyyy-MM-dd format.
     * <p>
     * 품목의 공급일자 (yyyy-MM-dd 형식)
     */
    private final String date; // yyyy-MM-dd

    /**
     * Item name.
     * <p>
     * 품목의 명칭 (품명)
     */
    private final String name;

    /**
     * Unit price per item.
     * <p>
     * 품목 1개당 가격 (단가)
     */
    private final Long unitPrice;

    /**
     * Quantity of items.
     * <p>
     * 품목의 수량
     */
    private final Integer quantity;

    /**
     * Supply cost.
     * Total cost before tax.
     * <p>
     * 세금을 제외한 총 공급가액
     */
    private final Long supplyCost;

    /**
     * Tax amount.
     * VAT or other applicable taxes.
     * <p>
     * 부가가치세 또는 기타 해당 세금 (세액)
     */
    private final Long tax;

    /**
     * Item specification or standards.
     * <p>
     * 품목의 규격 또는 표준
     */
    private final String specification;

    /**
     * Additional description for this item.
     * <p>
     * 이 품목에 대한 추가 설명 (비고)
     */
    private final String description;

    private TaxInvoiceLineItem() {
        this.date = null;
        this.name = null;
        this.unitPrice = null;
        this.quantity = null;
        this.supplyCost = null;
        this.tax = null;
        this.specification = null;
        this.description = null;
    }

    private TaxInvoiceLineItem(String date, String name, Long unitPrice, Integer quantity, Long supplyCost, Long tax,
            String specification, String description) {
        if (date == null)
            throw new NullPointerException("date is marked non-null but is null");
        if (name == null)
            throw new NullPointerException("name is marked non-null but is null");
        if (supplyCost == null)
            throw new NullPointerException("supplyCost is marked non-null but is null");
        this.date = date;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.supplyCost = supplyCost;
        this.tax = tax;
        this.specification = specification;
        this.description = description;
    }

    private TaxInvoiceLineItem(Builder builder) {
        this.date = builder.date;
        this.name = builder.name;
        this.unitPrice = builder.unitPrice;
        this.quantity = builder.quantity;
        this.supplyCost = builder.supplyCost;
        this.tax = builder.tax;
        this.specification = builder.specification;
        this.description = builder.description;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getSupplyCost() {
        return supplyCost;
    }

    public Long getTax() {
        return tax;
    }

    public String getSpecification() {
        return specification;
    }

    public String getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String date;
        private String name;
        private Long unitPrice;
        private Integer quantity;
        private Long supplyCost;
        private Long tax;
        private String specification;
        private String description;

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder unitPrice(Long unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder supplyCost(Long supplyCost) {
            this.supplyCost = supplyCost;
            return this;
        }

        public Builder tax(Long tax) {
            this.tax = tax;
            return this;
        }

        public Builder specification(String specification) {
            this.specification = specification;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public TaxInvoiceLineItem build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            if (name == null)
                throw new NullPointerException("name is marked non-null but is null");
            if (supplyCost == null)
                throw new NullPointerException("supplyCost is marked non-null but is null");
            return new TaxInvoiceLineItem(this);
        }
    }

    @Override
    public String toString() {
        return "TaxInvoiceLineItem{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", supplyCost=" + supplyCost +
                ", tax=" + tax +
                ", specification='" + specification + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaxInvoiceLineItem item = (TaxInvoiceLineItem) o;
        return Objects.equals(date, item.date) &&
                Objects.equals(name, item.name) &&
                Objects.equals(unitPrice, item.unitPrice) &&
                Objects.equals(quantity, item.quantity) &&
                Objects.equals(supplyCost, item.supplyCost) &&
                Objects.equals(tax, item.tax) &&
                Objects.equals(specification, item.specification) &&
                Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, name, unitPrice, quantity, supplyCost, tax, specification, description);
    }
}
