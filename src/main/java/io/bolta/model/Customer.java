package io.bolta.model;

import java.util.Objects;

/**
 * Represents a customer in the Bolta e-tax invoice system.
 * A customer can register their certificate to issue and receive e-tax invoices
 * through the Bolta platform.
 * <p>
 * 볼타 전자세금계산서 시스템의 고객을 나타냅니다.
 * 고객은 공동인증서를 등록하여 볼타 플랫폼을 통해 전자세금계산서를 발행하고 받을 수 있습니다.
 */
public final class Customer {
    /**
     * Business registration number (10-digit number assigned by the tax office)
     * <p>
     * 사업자등록번호 (세무서에서 부여한 10자리 번호)
     */
    private final String identificationNumber;

    /**
     * Tax registration ID (4-digit number for branch locations, optional)
     * <p>
     * 종사업자번호 (지점의 경우 4자리 번호, 선택사항)
     */
    private final String taxRegistrationId;

    /**
     * Organization name (business name or corporate name)
     * <p>
     * 상호/법인명 (사업자의 상호 또는 법인명)
     */
    private final String organizationName;

    /**
     * Representative name (name of the business representative)
     * <p>
     * 대표자명 (사업자 대표자의 성명)
     */
    private final String representativeName;

    /**
     * Business address
     * <p>
     * 사업장 주소
     */
    private final String address;

    /**
     * Business item (type of business, e.g., manufacturing, retail, services)
     * <p>
     * 업태 (예: 제조업, 도소매업, 서비스업)
     */
    private final String businessItem;

    /**
     * Business type (specific industry or product category)
     * <p>
     * 종목 (구체적인 업종 또는 품목)
     */
    private final String businessType;

    /**
     * Primary email address for receiving e-tax invoice receipts
     * <p>
     * 전자세금계산서 영수 수신용 이메일 주소
     */
    private final String email1;

    /**
     * Secondary email address for receiving e-tax invoice receipts
     * <p>
     * 전자세금계산서 영수 수신용 추가 이메일 주소
     */
    private final String email2;

    private Customer() {
        this.identificationNumber = null;
        this.taxRegistrationId = null;
        this.organizationName = null;
        this.representativeName = null;
        this.address = null;
        this.businessItem = null;
        this.businessType = null;
        this.email1 = null;
        this.email2 = null;
    }

    private Customer(String identificationNumber, String taxRegistrationId, String organizationName,
        String representativeName, String address, String businessItem, String businessType, String email1,
        String email2) {
        if (identificationNumber == null)
            throw new NullPointerException("identificationNumber is marked non-null but is null");
        if (organizationName == null)
            throw new NullPointerException("organizationName is marked non-null but is null");
        if (representativeName == null)
            throw new NullPointerException("representativeName is marked non-null but is null");
        this.identificationNumber = identificationNumber;
        this.taxRegistrationId = taxRegistrationId;
        this.organizationName = organizationName;
        this.representativeName = representativeName;
        this.address = address;
        this.businessItem = businessItem;
        this.businessType = businessType;
        this.email1 = email1;
        this.email2 = email2;
    }

    private Customer(Builder builder) {
        this.identificationNumber = builder.identificationNumber;
        this.taxRegistrationId = builder.taxRegistrationId;
        this.organizationName = builder.organizationName;
        this.representativeName = builder.representativeName;
        this.address = builder.address;
        this.businessItem = builder.businessItem;
        this.businessType = builder.businessType;
        this.email1 = builder.email1;
        this.email2 = builder.email2;
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

    public String getAddress() {
        return address;
    }

    public String getBusinessItem() {
        return businessItem;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getEmail1() {
        return email1;
    }

    public String getEmail2() {
        return email2;
    }

    /**
     * Creates a new builder for constructing a Customer instance.
     * <p>
     * Customer 인스턴스를 생성하기 위한 빌더를 생성합니다.
     *
     * @return a new Customer.Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing Customer instances with a fluent API.
     * <p>
     * Customer 인스턴스를 유연한 API로 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private String identificationNumber;
        private String taxRegistrationId;
        private String organizationName;
        private String representativeName;
        private String address;
        private String businessItem;
        private String businessType;
        private String email1;
        private String email2;

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

        public Builder email1(String email1) {
            this.email1 = email1;
            return this;
        }

        public Builder email2(String email2) {
            this.email2 = email2;
            return this;
        }

        public Customer build() {
            if (identificationNumber == null)
                throw new NullPointerException("identificationNumber is marked non-null but is null");
            if (organizationName == null)
                throw new NullPointerException("organizationName is marked non-null but is null");
            if (representativeName == null)
                throw new NullPointerException("representativeName is marked non-null but is null");
            return new Customer(this);
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", taxRegistrationId='" + taxRegistrationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", representativeName='" + representativeName + '\'' +
                ", address='" + address + '\'' +
                ", businessItem='" + businessItem + '\'' +
                ", businessType='" + businessType + '\'' +
                ", email1='" + email1 + '\'' +
                ", email2='" + email2 + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(identificationNumber, customer.identificationNumber) &&
                Objects.equals(taxRegistrationId, customer.taxRegistrationId) &&
                Objects.equals(organizationName, customer.organizationName) &&
                Objects.equals(representativeName, customer.representativeName) &&
                Objects.equals(address, customer.address) &&
                Objects.equals(businessItem, customer.businessItem) &&
                Objects.equals(businessType, customer.businessType) &&
                Objects.equals(email1, customer.email1) &&
                Objects.equals(email2, customer.email2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificationNumber, taxRegistrationId, organizationName, representativeName, address,
                businessItem, businessType, email1, email2);
    }
}
