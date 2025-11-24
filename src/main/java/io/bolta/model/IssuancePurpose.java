package io.bolta.model;

/**
 * Represents the purpose of e-tax invoice issuance.
 * <p>
 * 전자세금계산서 발행 목적을 나타냅니다.
 */
public enum IssuancePurpose {
    /**
     * Receipt - issued for completed transactions where payment has been made
     * <p>
     * 영수 - 대금을 받고 거래가 완료된 경우 발행
     */
    RECEIPT,

    /**
     * Claim - issued for billing purposes before payment is received
     * <p>
     * 청구 - 대금을 받기 전 청구 목적으로 발행
     */
    CLAIM
}
