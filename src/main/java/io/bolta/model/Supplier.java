package io.bolta.model;

import java.util.Objects;

/**
 * Represents the e-Tax Invoice Supplier (공급자) in an e-tax invoice.
 * 전자세금계산서의 공급자를 나타냅니다.
 * <p>
 * The supplier is the party providing goods or services and issuing the
 * invoice.
 * 공급자는 상품이나 서비스를 제공하고 세금계산서를 발행하는 당사자입니다.
 */
public final class Supplier {
    /**
     * Business registration number (사업자등록번호)
     * 10-digit number assigned by the tax office
     * 세무서에서 부여한 10자리 사업자등록번호
     */
    private final String identificationNumber;

    /**
     * Tax registration ID (종사업자번호)
     * 4-digit tax registration ID for branch locations (optional)
     * 지점의 경우 4자리 종사업자번호 (선택사항)
     */
    private final String taxRegistrationId;

    /**
     * Organization name (상호/법인명)
     * Business name or corporate name of the supplier
     * 공급자의 상호 또는 법인명
     */
    private final String organizationName;

    /**
     * Representative name (대표자명)
     * Name of the supplier's representative
     * 공급자 대표자의 성명
     */
    private final String representativeName;

    /**
     * Manager information for the supplier
     * 공급자의 담당자 정보
     */
    private final Manager manager;

    /**
     * Business address of the supplier
     * 공급자의 사업장 주소
     */
    private final String address;

    /**
     * Business item (업태)
     * Type of business (e.g., manufacturing, retail, services)
     * 업태 (예: 제조업, 도소매업, 서비스업)
     */
    private final String businessItem;

    /**
     * Business type (종목)
     * Specific industry or product category
     * 종목 (구체적인 업종 또는 품목)
     */
    private final String businessType;

    private Supplier() {
        this.identificationNumber = null;
        this.taxRegistrationId = null;
        this.organizationName = null;
        this.representativeName = null;
        this.manager = null;
        this.address = null;
        this.businessItem = null;
        this.businessType = null;
    }

    private Supplier(String identificationNumber, String taxRegistrationId, String organizationName,
            String representativeName, Manager manager, String address, String businessItem,
            String businessType) {
        if (identificationNumber == null)
            throw new NullPointerException("identificationNumber is marked non-null but is null");
        if (organizationName == null)
            throw new NullPointerException("organizationName is marked non-null but is null");
        if (representativeName == null)
            throw new NullPointerException("representativeName is marked non-null but is null");
        if (manager == null)
            throw new NullPointerException("manager is marked non-null but is null");
        this.identificationNumber = identificationNumber;
        this.taxRegistrationId = taxRegistrationId;
        this.organizationName = organizationName;
        this.representativeName = representativeName;
        this.manager = manager;
        this.address = address;
        this.businessItem = businessItem;
        this.businessType = businessType;
    }

    private Supplier(Builder builder) {
        this.identificationNumber = builder.identificationNumber;
        this.taxRegistrationId = builder.taxRegistrationId;
        this.organizationName = builder.organizationName;
        this.representativeName = builder.representativeName;
        this.manager = builder.manager;
        this.address = builder.address;
        this.businessItem = builder.businessItem;
        this.businessType = builder.businessType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getTaxRegistrationId() {
        return taxRegistrationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public Manager getManager() {
        return manager;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessItem() {
        return businessItem;
    }

    public String getBusinessType() {
        return businessType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String identificationNumber;
        private String taxRegistrationId;
        private String organizationName;
        private String representativeName;
        private Manager manager;
        private String address;
        private String businessItem;
        private String businessType;

        public Builder identificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
            return this;
        }

        public Builder taxRegistrationId(String taxRegistrationId) {
            this.taxRegistrationId = taxRegistrationId;
            return this;
        }

        public Builder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public Builder representativeName(String representativeName) {
            this.representativeName = representativeName;
            return this;
        }

        public Builder manager(Manager manager) {
            this.manager = manager;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder businessItem(String businessItem) {
            this.businessItem = businessItem;
            return this;
        }

        public Builder businessType(String businessType) {
            this.businessType = businessType;
            return this;
        }

        public Supplier build() {
            if (identificationNumber == null)
                throw new NullPointerException("identificationNumber is marked non-null but is null");
            if (organizationName == null)
                throw new NullPointerException("organizationName is marked non-null but is null");
            if (representativeName == null)
                throw new NullPointerException("representativeName is marked non-null but is null");
            if (manager == null)
                throw new NullPointerException("manager is marked non-null but is null");
            return new Supplier(this);
        }
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", taxRegistrationId='" + taxRegistrationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", representativeName='" + representativeName + '\'' +
                ", manager=" + manager +
                ", address='" + address + '\'' +
                ", businessItem='" + businessItem + '\'' +
                ", businessType='" + businessType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(identificationNumber, supplier.identificationNumber) &&
                Objects.equals(taxRegistrationId, supplier.taxRegistrationId) &&
                Objects.equals(organizationName, supplier.organizationName) &&
                Objects.equals(representativeName, supplier.representativeName) &&
                Objects.equals(manager, supplier.manager) &&
                Objects.equals(address, supplier.address) &&
                Objects.equals(businessItem, supplier.businessItem) &&
                Objects.equals(businessType, supplier.businessType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificationNumber, taxRegistrationId, organizationName, representativeName,
                manager, address, businessItem, businessType);
    }
}
