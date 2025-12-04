package io.bolta.model;

import java.util.Objects;

/**
 * Request model for correcting a tax invoice that was issued in duplicate by mistake.
 * Used when the same tax invoice was accidentally issued more than once.
 * <p>
 * 착오로 이중 발급된 세금계산서 정정 요청 모델입니다.
 * 동일한 세금계산서가 실수로 두 번 이상 발급되었을 때 사용됩니다.
 *
 * @see io.bolta.resource.TaxInvoiceResource#issueDuplicateCorrection(String, DuplicateCorrectionRequest)
 */
public final class DuplicateCorrectionRequest {
    /**
     * Date when the duplicate issuance occurred (yyyy-MM-dd)
     * 수정 사유 발생일
     */
    private final String date;

    private DuplicateCorrectionRequest() {
        this.date = null;
    }

    private DuplicateCorrectionRequest(String date) {
        if (date == null)
            throw new NullPointerException("date is marked non-null but is null");
        this.date = date;
    }

    private DuplicateCorrectionRequest(Builder builder) {
        this.date = builder.date;
    }

    /**
     * Returns the date when the duplicate issuance occurred.
     * <p>
     * 이중 발급이 발생한 날짜를 반환합니다.
     *
     * @return the correction date in yyyy-MM-dd format
     */
    public String getDate() {
        return date;
    }

    /**
     * Creates a new builder for DuplicateCorrectionRequest.
     * <p>
     * DuplicateCorrectionRequest를 위한 새 빌더를 생성합니다.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing DuplicateCorrectionRequest instances.
     * <p>
     * DuplicateCorrectionRequest 인스턴스를 생성하기 위한 빌더입니다.
     */
    public static class Builder {
        private String date;

        /**
         * Sets the date when the duplicate issuance occurred.
         * <p>
         * 이중 발급이 발생한 날짜를 설정합니다.
         *
         * @param date the correction date in yyyy-MM-dd format
         * @return this builder
         */
        public Builder date(String date) {
            this.date = date;
            return this;
        }

        /**
         * Builds a new DuplicateCorrectionRequest instance.
         * <p>
         * 새 DuplicateCorrectionRequest 인스턴스를 생성합니다.
         *
         * @return the configured DuplicateCorrectionRequest
         * @throws NullPointerException if date is null
         */
        public DuplicateCorrectionRequest build() {
            if (date == null)
                throw new NullPointerException("date is marked non-null but is null");
            return new DuplicateCorrectionRequest(this);
        }
    }

    @Override
    public String toString() {
        return "DuplicateCorrectionRequest{" +
                "date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DuplicateCorrectionRequest that = (DuplicateCorrectionRequest) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
