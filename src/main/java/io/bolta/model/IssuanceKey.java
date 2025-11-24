package io.bolta.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

/**
 * Represents a unique issuance key for an e-tax invoice.
 * This key is returned when an e-tax invoice is successfully issued and can be
 * used to query the invoice details later.
 * <p>
 * 전자세금계산서의 고유한 발급 키를 나타냅니다.
 * 이 키는 전자세금계산서가 성공적으로 발급될 때 반환되며, 나중에 세금계산서 상세 정보를 조회하는 데 사용할 수 있습니다.
 */
public final class IssuanceKey {
    /**
     * The unique identifier value for this issuance
     * <p>
     * 이 발급의 고유 식별자 값
     */
    private final String value;

    private IssuanceKey() {
        this.value = null;
    }

    private IssuanceKey(String value) {
        this.value = value;
    }

    /**
     * Creates an IssuanceKey instance from a string value.
     * <p>
     * 문자열 값으로부터 IssuanceKey 인스턴스를 생성합니다.
     *
     * @param value the issuance key value
     * @return a new IssuanceKey instance
     */
    @JsonCreator
    public static IssuanceKey of(String value) {
        if (value == null) {
            throw new NullPointerException("value is marked non-null but is null");
        }
        return new IssuanceKey(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IssuanceKey that = (IssuanceKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
