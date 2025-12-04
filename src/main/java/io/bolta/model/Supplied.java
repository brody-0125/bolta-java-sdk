package io.bolta.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the e-Tax Invoice Supplied Party (공급받는자) in an e-tax invoice.
 * 전자세금계산서의 공급받는자를 나타냅니다.
 * <p>
 * The supplied party is the recipient of goods or services who receives the
 * invoice.
 * 공급받는자는 상품이나 서비스를 받고 세금계산서를 수령하는 당사자입니다.
 */
public final class Supplied {
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
     * Business name or corporate name of the supplied party
     * 공급받는자의 상호 또는 법인명
     */
    private final String organizationName;

    /**
     * Representative name (대표자명)
     * Name of the supplied party's representative
     * 공급받는자 대표자의 성명
     */
    private final String representativeName;

    /**
     * Business address of the supplied party
     * 공급받는자의 사업장 주소
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

    /**
     * List of manager contacts for the supplied party
     * 공급받는자의 담당자 목록
     */
    private final List<Manager> managers;

    private Supplied() {
        this.identificationNumber = null;
        this.taxRegistrationId = null;
        this.organizationName = null;
        this.representativeName = null;
        this.address = null;
        this.businessItem = null;
        this.businessType = null;
        this.managers = null;
    }

    private Supplied(String identificationNumber, String taxRegistrationId, String organizationName,
            String representativeName, String address, String businessItem, String businessType,
            List<Manager> managers) {
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
        this.managers = managers;
    }

    private Supplied(Builder builder) {
        this.identificationNumber = builder.identificationNumber;
        this.taxRegistrationId = builder.taxRegistrationId;
        this.organizationName = builder.organizationName;
        this.representativeName = builder.representativeName;
        this.address = builder.address;
        this.businessItem = builder.businessItem;
        this.businessType = builder.businessType;
        this.managers = builder.managers;
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

    public List<Manager> getManagers() {
        return managers == null ? null : Collections.unmodifiableList(managers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String identificationNumber;
        private String taxRegistrationId;
        private String organizationName;
        private String representativeName;
        private String address;
        private String businessItem;
        private String businessType;
        private List<Manager> managers;

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

        public Builder managers(List<Manager> managers) {
            this.managers = managers;
            return this;
        }

        public Supplied build() {
            if (identificationNumber == null)
                throw new NullPointerException("identificationNumber is marked non-null but is null");
            if (organizationName == null)
                throw new NullPointerException("organizationName is marked non-null but is null");
            if (representativeName == null)
                throw new NullPointerException("representativeName is marked non-null but is null");
            return new Supplied(this);
        }
    }

    @Override
    public String toString() {
        return "Supplied{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", taxRegistrationId='" + taxRegistrationId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", representativeName='" + representativeName + '\'' +
                ", address='" + address + '\'' +
                ", businessItem='" + businessItem + '\'' +
                ", businessType='" + businessType + '\'' +
                ", managers=" + managers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Supplied supplied = (Supplied) o;
        return Objects.equals(identificationNumber, supplied.identificationNumber) &&
                Objects.equals(taxRegistrationId, supplied.taxRegistrationId) &&
                Objects.equals(organizationName, supplied.organizationName) &&
                Objects.equals(representativeName, supplied.representativeName) &&
                Objects.equals(address, supplied.address) &&
                Objects.equals(businessItem, supplied.businessItem) &&
                Objects.equals(businessType, supplied.businessType) &&
                Objects.equals(managers, supplied.managers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificationNumber, taxRegistrationId, organizationName, representativeName, address,
                businessItem, businessType, managers);
    }
}
