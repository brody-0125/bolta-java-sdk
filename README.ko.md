# Bolta Java SDK (비공식)

**한국어** | **[English](README.md)**

> ⚠️ **이것은 비공식 SDK입니다** - 이 SDK는 볼타(Bolta)에서 공식적으로 유지 관리하거나 승인하지 않습니다. 사용에 따른 책임은 사용자에게 있습니다.

[Bolta](https://bolta.io) 전자세금계산서 API를 위한 개발자 친화적인 Java SDK입니다.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 요구사항

- Java 8 이상
- Gradle 또는 Maven

## 설치

### Gradle

`build.gradle` 또는 `build.gradle.kts`에 추가:

```kotlin
dependencies {
    implementation 'io.bolta:bolta-java-sdk:0.0.1'
}
```

### Maven

`pom.xml`에 추가:

```xml
<dependency>
    <groupId>io.bolta</groupId>
    <artifactId>bolta-java-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 빠른 시작

### SDK 초기화

```java
import io.bolta.BoltaApp;

BoltaApp app = BoltaApp.builder()
    .apiKey("your-api-key-here")
    .build();
```

### 전자세금계산서 발행

```java
import io.bolta.model.*;
import java.util.List;

TaxInvoice invoice = TaxInvoice.builder()
    .date("2024-01-15")
    .purpose(IssuancePurpose.RECEIPT)  // 영수
    .supplier(TaxInvoice.Supplier.builder()
        .identificationNumber("1234567890")
        .organizationName("공급자 회사")
        .representativeName("홍길동")
        .managerEmail("manager@supplier.com")
        .address("서울시 강남구 테헤란로 123")
        .businessItem("제조업")
        .businessType("전자제품")
        .build())
    .supplied(TaxInvoice.Supplied.builder()
        .identificationNumber("0987654321")
        .organizationName("고객 회사")
        .representativeName("김철수")
        .address("서울시 서초구 서초대로 456")
        .businessItem("도소매업")
        .businessType("전자제품 판매")
        .email1("billing@customer.com")
        .build())
    .items(List.of(
        TaxInvoice.Item.builder()
            .date("2024-01-15")
            .name("제품명")
            .quantity(10)
            .unitPrice(100000L)
            .supplyCost(1000000L)  // 공급가액
            .tax(100000L)          // 세액
            .build()
    ))
    .description("1월 정기 납품분")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice);
System.out.println("발급된 세금계산서: " + key.getValue());
```

### 전자세금계산서 조회

```java
TaxInvoice invoice = app.taxInvoices().get("발급키");
System.out.println("공급자: " + invoice.getSupplier().getOrganizationName());
```

### 고객 관리

```java
// 고객 생성
Customer customer = Customer.builder()
    .identificationNumber("1234567890")
    .organizationName("고객 회사명")
    .representativeName("대표자명")
    .address("사업장 주소")
    .businessItem("업태")
    .businessType("종목")
    .email1("customer@example.com")
    .build();

app.customers().create(customer);

// 고객 조회
Customer retrieved = app.customers().get("1234567890");

// 고객 공동인증서 등록해제
app.customers().delete("1234567890");
```

### 다중 고객 플랫폼

여러 고객을 관리하는 플랫폼의 경우:

```java
RequestOptions options = RequestOptions.builder()
    .customerKey("고객별-키")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

### 클라이언트 관리번호로 요청 추적

자체 관리번호로 요청을 추적할 수 있습니다:

```java
RequestOptions options = RequestOptions.builder()
    .clientReferenceId("ABC_123")  // 고유 관리번호
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

플랫폼 시나리오에서 고객 키와 함께 사용:

```java
RequestOptions options = RequestOptions.builder()
    .customerKey("customer-001")
    .clientReferenceId("주문_2024_001")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

### 비동기 작업

```java
import java.util.concurrent.CompletableFuture;

CompletableFuture<IssuanceKey> future = app.taxInvoices().issueAsync(invoice);

future.thenAccept(key -> {
    System.out.println("발급 완료: " + key.getValue());
}).exceptionally(error -> {
    error.printStackTrace();
    return null;
});
```

## 오류 처리

```java
import io.bolta.exception.*;

try {
    IssuanceKey key = app.taxInvoices().issue(invoice);
} catch (BoltaApiException e) {
    System.err.println("API 오류: " + e.getMessage());
    System.err.println("상태 코드: " + e.getStatusCode());
    System.err.println("응답 본문: " + e.getResponseBody());
} catch (BoltaException e) {
    System.err.println("SDK 오류: " + e.getMessage());
}
```

## 고급 설정

```java
BoltaConfig config = BoltaConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://xapi.bolta.io")
    .connectTimeoutMillis(10000)  // 연결 타임아웃
    .readTimeoutMillis(30000)     // 읽기 타임아웃
    .writeTimeoutMillis(30000)    // 쓰기 타임아웃
    .build();

BoltaApp app = BoltaApp.builder()
    .config(config)
    .build();
```

## 문서

- 📖 [Bolta API 문서](https://api-docs.bolta.io)
- 📚 [사용 예제](EXAMPLES.md)

## 소스에서 빌드

```bash
git clone https://github.com/yourusername/bolta-java-sdk.git
cd bolta-java-sdk
./gradlew build
```

## 테스트

```bash
./gradlew test
```

## 기여하기

기여를 환영합니다! Pull Request를 자유롭게 제출해 주세요.

## 면책 조항

⚠️ **중요**: 이것은 **비공식 SDK**이며 Bolta와 관련이 없고, Bolta에서 유지 관리하거나 승인하지 않습니다.

- 이 SDK는 "있는 그대로" 어떠한 보증 없이 제공됩니다
- 프로덕션 환경에서 사용 시 책임은 사용자에게 있습니다
- 공식 지원은 Bolta에 직접 문의하시기 바랍니다

## 라이선스

이 프로젝트는 MIT 라이선스에 따라 라이선스가 부여됩니다 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 지원

- 📖 [API 문서](https://api-docs.bolta.io)
- 📧 SDK 이슈: [GitHub Issues](https://github.com/yourusername/bolta-java-sdk/issues)
- 🌐 Bolta API 지원: Bolta에 직접 문의

---

**참고**: Bolta 및 모든 관련 상표는 해당 소유자의 자산입니다.
