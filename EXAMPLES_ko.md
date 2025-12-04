# Bolta Java SDK - 사용 예제

> **비공식 SDK** - Bolta와 제휴하거나 보증하지 않습니다

이 문서는 Bolta Java SDK의 실용적인 사용 예제를 발행 유형 및 사용 사례별로 정리합니다.

**[English Version](EXAMPLES.md)**

## 목차

- [빠른 시작](#빠른-시작)
- [정발행](#정발행)
  - [영수 - 대금 수령 후](#영수---대금-수령-후)
  - [청구 - 대금 수령 전](#청구---대금-수령-전)
- [면세 및 영세율 발행](#면세-및-영세율-발행)
  - [면세](#면세)
  - [영세율](#영세율)
- [역발행](#역발행)
  - [사용 시점](#역발행-사용-시점)
  - [배치 처리 예제](#배치-처리-예제)
- [수정발행](#수정발행)
- [오류 처리](#오류-처리)
- [고급 설정](#고급-설정)

---

## 빠른 시작

```java
import io.bolta.BoltaApp;
import io.bolta.BoltaConfig;

BoltaConfig config = BoltaConfig.builder()
    .apiKey("live_your_api_key")
    .build();

BoltaApp app = BoltaApp.builder()
    .config(config)
    .build();
```

---

## 정발행

정발행은 **공급자가 직접** 세금계산서를 발행하는 방식입니다. 발행 목적에 따라 **영수**와 **청구**로 구분됩니다.

### 영수 - 대금 수령 후

**영수**는 대금을 **이미 받은 후** 발행하는 세금계산서입니다.

**처리 순서:**
```
거래 발생 → 입금 확인 → 정보 요청 → 정보 수령 → 세금계산서 발행
```

**사용 시점:**
- 현금/카드 결제 완료 후
- 월말 정산 완료 후
- 선불 결제(선급금, 계약금) 확인 후

```java
import io.bolta.model.*;
import java.util.List;

public class ReceiptExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("live_your_api_key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // 공급자 정보
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("(주)공급회사")
            .representativeName("김대표")
            .address("서울특별시 강남구 테헤란로 123")
            .businessType("도소매업")
            .businessItem("전자제품")
            .manager(Manager.builder()
                .name("이담당")
                .email("manager@supplier.co.kr")
                .telephone("02-1234-5678")
                .build())
            .build();

        // 공급받는자 정보
        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("(주)구매회사")
            .representativeName("박대표")
            .address("서울특별시 서초구 서초대로 456")
            .businessType("제조업")
            .businessItem("기계제조")
            .managers(List.of(
                Manager.builder()
                    .name("최담당")
                    .email("buyer@purchaser.co.kr")
                    .telephone("02-9876-5432")
                    .build()
            ))
            .build();

        // 품목
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("노트북")
                .specification("15인치, 16GB RAM")
                .quantity(10)
                .unitPrice(1500000L)
                .supplyCost(15000000L)    // 1,500만원
                .tax(1500000L)            // 150만원 (10%)
                .build()
        );

        // 영수 세금계산서 (대금 수령 완료)
        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-15")
            .purpose(IssuancePurpose.RECEIPT)  // 영수
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("12월 납품분 - 대금 수령 완료")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("영수 세금계산서 발행 완료: " + key.getValue());
        } catch (Exception e) {
            System.err.println("발행 실패: " + e.getMessage());
        }
    }
}
```

---

### 청구 - 대금 수령 전

**청구**는 대금을 **받기 전에** 발행하는 세금계산서입니다.

**처리 순서:**
```
거래 발생 → 정보 요청 → 정보 수령 → 세금계산서 발행 → 입금 확인
```

**사용 시점:**
- 후불 결제 거래
- 월말 마감 후 익월 청구
- 건설 기성금 청구
- 프로젝트 마일스톤 청구

**핵심 차이**: 청구는 발행 후 입금을 기다리고, 영수는 입금 후 발행합니다.

```java
import io.bolta.model.*;
import java.util.List;

public class ClaimExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("live_your_api_key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // 공급자/공급받는자 설정 (위 예제와 동일)
        Supplier supplier = createSupplier();
        Supplied supplied = createSupplied();

        // 품목: 용역 제공
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-31")
                .name("시스템 개발 용역")
                .specification("1차 마일스톤 - 설계 완료")
                .quantity(1)
                .unitPrice(50000000L)
                .supplyCost(50000000L)    // 5,000만원
                .tax(5000000L)            // 500만원
                .description("계약금액 2억 중 25% - 설계 완료 기성")
                .build()
        );

        // 청구 세금계산서 (대금 미수령)
        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-31")
            .purpose(IssuancePurpose.CLAIM)  // 청구
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("시스템 개발 용역 1차 마일스톤 청구")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("청구 세금계산서 발행 완료: " + key.getValue());
        } catch (Exception e) {
            System.err.println("발행 실패: " + e.getMessage());
        }
    }
}
```

---

## 면세 및 영세율 발행

### 면세

**면세**는 부가가치세 **과세 대상이 아닌** 재화/용역에 대한 거래입니다.

> **중요**: 면세사업자는 **세금계산서**가 아닌 **계산서**를 발행합니다.
> 세금계산서는 부가세를 포함하지만, 계산서는 부가세가 없습니다.

**면세 대상 업종:**
- **농·축·수산물**: 미가공 농산물, 축산물, 수산물 도·소매
- **의료업**: 병원, 의원, 한의원
- **교육 서비스업**: 학원, 교습소
- **출판·신문**: 도서, 신문, 잡지
- **복지 서비스업**: 보육시설, 사회복지 서비스

**발행 가능 사업자:**
- 면세사업자로 등록된 사업자
- 과세/면세 겸업 사업자 (면세 품목에 한해)

**주의사항:**
- 면세사업자도 **매입 시 세금계산서를 수취**해야 합니다
- 매입 세금계산서 누락 시 경비 불인정으로 종합소득세 부담 증가
- 법인사업자는 전자계산서 의무 발급 대상

```java
import io.bolta.model.*;
import java.util.List;

public class TaxExemptExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("live_your_api_key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // 공급자: 면세사업자 (예: 농산물 도매상)
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("청과농산")
            .representativeName("김농산")
            .address("경기도 가평군 가평읍 경춘로 123")
            .businessType("도소매업")
            .businessItem("농산물 도매")  // 면세 품목
            .manager(Manager.builder()
                .name("이담당")
                .email("sales@farm.co.kr")
                .telephone("031-123-4567")
                .build())
            .build();

        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("(주)식품마트")
            .representativeName("박마트")
            .address("서울특별시 성동구 뚝섬로 456")
            .businessType("도소매업")
            .businessItem("종합소매")
            .build();

        // 품목: 면세 농산물 (세금 = 0)
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("국내산 사과")
                .specification("부사, 10kg 박스")
                .quantity(100)
                .unitPrice(30000L)
                .supplyCost(3000000L)     // 300만원
                .tax(0L)                  // 면세: 세금 0원
                .build(),
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("국내산 배")
                .specification("신고, 15kg 박스")
                .quantity(80)
                .unitPrice(45000L)
                .supplyCost(3600000L)     // 360만원
                .tax(0L)                  // 면세: 세금 0원
                .build()
        );

        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-15")
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("12월 농산물 납품분 (면세)")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("면세 계산서 발행 완료: " + key.getValue());
            System.out.println("공급가액: 6,600,000원, 세액: 0원");
        } catch (Exception e) {
            System.err.println("발행 실패: " + e.getMessage());
        }
    }
}
```

---

### 영세율

**영세율**은 부가가치세법에서 규정한 **과세 거래**이지만, 세율이 **0%**입니다.

> **핵심**: 부가세를 내지 않으면서도 **매입세액 환급**이 가능합니다.
> 영세율 거래는 **세금계산서 발행이 필수**입니다.

**영세율 적용 대상:**
- **수출 관련**: 직접 수출, 내국신용장(Local L/C), 보세구역 거래
- **국제 운송 용역**: 국경 간 화물 운송, 외국 항행 선박/항공기
- **국외 제공 용역**: 해외 건설, 기술 컨설팅
- **전략산업 수출**: 반도체, 이차전지, 신재생에너지, 문화콘텐츠

**발행 가능 사업자:**
- 수출업자 (직접 수출, 중계 수출)
- 외화 획득 사업자
- 국제 운송 사업자
- 내국신용장 거래 사업자

```java
import io.bolta.model.*;
import java.util.List;

public class ZeroRateExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("live_your_api_key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // 공급자: 수출업체
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("(주)글로벌테크")
            .representativeName("김수출")
            .address("경기도 화성시 동탄산단로 123")
            .businessType("제조업")
            .businessItem("전자부품 제조 및 수출")
            .manager(Manager.builder()
                .name("이무역")
                .email("export@globaltech.co.kr")
                .telephone("031-234-5678")
                .build())
            .build();

        // 공급받는자: 국내 중개상 (수출 대행)
        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("(주)무역중개")
            .representativeName("박무역")
            .address("서울특별시 강남구 테헤란로 789")
            .businessType("도소매업")
            .businessItem("무역 중개")
            .build();

        // 품목: 수출 재화 (영세율 적용)
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-20")
                .name("반도체 웨이퍼")
                .specification("300mm, 수출용")
                .quantity(1000)
                .unitPrice(500000L)
                .supplyCost(500000000L)   // 5억원
                .tax(0L)                  // 영세율: 세금 0원
                .description("미국 수출분 (B/L No: ABCD1234)")
                .build()
        );

        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-20")
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("반도체 수출 (영세율)")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("영세율 세금계산서 발행 완료: " + key.getValue());
            System.out.println("공급가액: 500,000,000원, 세액: 0원 (영세율)");
        } catch (Exception e) {
            System.err.println("발행 실패: " + e.getMessage());
        }
    }
}
```

**면세 vs 영세율 차이점:**

| 구분 | 면세 | 영세율 |
|------|------|--------|
| 세법 성격 | **과세 대상 아님** | **과세 거래** (세율만 0%) |
| 발행 문서 | **계산서** (전자계산서) | **세금계산서** (필수) |
| 매입세액 공제 | **불가** | **가능** (환급 가능) |
| 대상 | 기초필수품, 의료, 교육 등 | 수출, 외화획득 등 |

> **Tip**: 영세율은 수출 촉진을 위한 정책적 혜택으로, 사업자가 매입 시 부담한 부가세를 환급받을 수 있습니다.

---

## 역발행

### 역발행 사용 시점

**역발행**은 **공급받는자**가 세금계산서 발행을 요청하고, **공급자**가 승인하는 방식입니다.

**사용 시점:**
- 공급자가 세금계산서 발행을 누락하거나 지연하는 경우
- 대기업이 중소 협력업체의 세금계산서를 일괄 관리하는 경우
- 다수의 공급자로부터 정기적으로 매입하는 경우
- **배치 처리**로 대량의 세금계산서를 효율적으로 처리하는 경우

**프로세스:**
1. 공급받는자가 역발행 요청 생성
2. 시스템이 공급자에게 승인 URL 발송
3. 공급자가 URL 접속하여 승인
4. 세금계산서 자동 발행

---

### 배치 처리 예제 (Spring Batch)

대기업의 구매팀이 월말에 다수 협력업체의 세금계산서를 일괄 요청하는 Spring Batch 기반 애플리케이션 예시입니다.

#### 1. 배치 설정 (BatchConfig.java)

```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ReverseIssuanceBatchConfig {

    @Bean
    public Job reverseIssuanceJob(JobRepository jobRepository, Step reverseIssuanceStep) {
        return new JobBuilder("reverseIssuanceJob", jobRepository)
            .start(reverseIssuanceStep)
            .build();
    }

    @Bean
    public Step reverseIssuanceStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    ReverseIssuanceTasklet tasklet) {
        return new StepBuilder("reverseIssuanceStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }
}
```

#### 2. Tasklet 구현 (ReverseIssuanceTasklet.java)

```java
import io.bolta.BoltaApp;
import io.bolta.exception.BoltaApiException;
import io.bolta.model.*;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 월말 역발행 배치 Tasklet
 *
 * 실행 시점: 매월 말일 23:00 (스케줄러 설정)
 * 처리 내용:
 * - ERP에서 미발행 매입 데이터 조회
 * - 협력업체별로 역발행 요청 생성
 * - 승인 URL을 이메일로 발송
 */
@Component
public class ReverseIssuanceTasklet implements Tasklet {

    private final BoltaApp boltaApp;
    private final PurchaseRepository purchaseRepository;
    private final EmailService emailService;

    public ReverseIssuanceTasklet(BoltaApp boltaApp,
                                   PurchaseRepository purchaseRepository,
                                   EmailService emailService) {
        this.boltaApp = boltaApp;
        this.purchaseRepository = purchaseRepository;
        this.emailService = emailService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        // 1. 미발행 매입 데이터 조회
        List<PurchaseOrder> pendingOrders = purchaseRepository
            .findByTaxInvoiceStatusAndClosingMonth("PENDING", getCurrentMonth());

        int successCount = 0;
        int failCount = 0;

        // 2. 건별 역발행 요청 처리
        for (PurchaseOrder order : pendingOrders) {
            try {
                processReverseIssuance(order);
                successCount++;
                contribution.incrementReadCount();
            } catch (Exception e) {
                failCount++;
                contribution.incrementReadSkipCount();
                // 실패 건은 로그 기록 후 계속 진행
                logFailure(order, e);
            }
        }

        // 3. 처리 결과를 ExecutionContext에 저장
        chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .put("processedCount", successCount + failCount);

        return RepeatStatus.FINISHED;
    }

    private void processReverseIssuance(PurchaseOrder order) {
        // 역발행 요청 생성
        TaxInvoiceIssuanceRequest request = TaxInvoiceIssuanceRequest.builder()
            .date(order.getTransactionDate())
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(buildSupplier(order.getSupplier()))
            .supplied(buildOurCompany())
            .items(buildLineItems(order.getItems()))
            .description(order.getDescription())
            .build();

        // 역발행 요청 실행
        IssuanceKey requestKey = boltaApp.taxInvoiceIssuanceRequests()
            .request(request);

        // 승인 URL 조회
        String grantUrl = boltaApp.taxInvoiceIssuanceRequests()
            .getGrantUrl(requestKey.getValue());

        // DB 상태 업데이트
        order.setTaxInvoiceStatus("REQUESTED");
        order.setIssuanceKey(requestKey.getValue());
        purchaseRepository.save(order);

        // 공급자에게 승인 요청 이메일 발송
        emailService.sendApprovalRequest(
            order.getSupplier().getEmail(),
            order.getSupplier().getOrganizationName(),
            grantUrl
        );
    }

    private Supplier buildSupplier(SupplierInfo info) {
        return Supplier.builder()
            .identificationNumber(info.getBizNo())
            .organizationName(info.getOrganizationName())
            .representativeName(info.getCeoName())
            .address(info.getAddress())
            .businessType(info.getBusinessType())
            .businessItem(info.getBusinessItem())
            .manager(Manager.builder()
                .email(info.getEmail())
                .name(info.getManagerName())
                .telephone(info.getTelephone())
                .build())
            .build();
    }

    private Supplied buildOurCompany() {
        return Supplied.builder()
            .identificationNumber("1234567890")
            .organizationName("(주)대기업")
            .representativeName("김대표")
            .address("서울특별시 영등포구 여의대로 123")
            .businessType("제조업")
            .businessItem("전자제품 제조")
            .managers(List.of(
                Manager.builder()
                    .name("구매팀")
                    .email("procurement@bigcorp.co.kr")
                    .telephone("02-1234-5678")
                    .build()
            ))
            .build();
    }

    private List<TaxInvoiceLineItem> buildLineItems(List<PurchaseItem> items) {
        return items.stream()
            .map(item -> TaxInvoiceLineItem.builder()
                .date(item.getSupplyDate())
                .name(item.getProductName())
                .specification(item.getSpecification())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .supplyCost(item.getSupplyCost())
                .tax(item.getTax())
                .build())
            .toList();
    }

    private void logFailure(PurchaseOrder order, Exception e) {
        // 실패 로그 저장 (별도 테이블 또는 로깅 시스템)
    }

    private String getCurrentMonth() {
        return java.time.YearMonth.now().toString();
    }
}
```

#### 3. BoltaApp Bean 설정

```java
import io.bolta.BoltaApp;
import io.bolta.BoltaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoltaConfig {

    @Value("${bolta.api-key}")
    private String apiKey;

    @Bean
    public BoltaApp boltaApp() {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey(apiKey)
            .connectTimeoutMillis(10000)
            .readTimeoutMillis(60000)
            .build();

        return BoltaApp.builder()
            .config(config)
            .build();
    }
}
```

#### 4. 스케줄러 설정

```java
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReverseIssuanceScheduler {

    private final JobLauncher jobLauncher;
    private final Job reverseIssuanceJob;

    public ReverseIssuanceScheduler(JobLauncher jobLauncher,
                                     Job reverseIssuanceJob) {
        this.jobLauncher = jobLauncher;
        this.reverseIssuanceJob = reverseIssuanceJob;
    }

    // 매월 말일 23:00 실행
    @Scheduled(cron = "0 0 23 L * ?")
    public void runMonthlyReverseIssuance() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(reverseIssuanceJob, params);
    }
}
```

**역발행 요청 취소:**

```java
// 공급자가 승인하기 전에 요청 취소
app.taxInvoiceIssuanceRequests().cancel(requestKey);
```

---

## 수정발행

기 발행된 세금계산서를 수정해야 하는 경우입니다.

| 수정 사유 | 메서드 | 사용 시점 |
|----------|--------|----------|
| 계약 해제 | `issueContractTermination()` | 계약 취소/해지 |
| 공급가액 변동 | `issueAmendSupplyCost()` | 반품, 할인, 에누리 |
| 이중발급 정정 | `issueDuplicateCorrection()` | 실수로 중복 발행 |
| 기재사항 정정 | `issueErrorCorrection()` | 금액, 품목 등 오기재 |

```java
// 계약 해제
ContractTerminationRequest request = ContractTerminationRequest.builder()
    .date("2024-12-20")
    .build();
app.taxInvoices().issueContractTermination(originalKey, request);

// 이중발급 정정
DuplicateCorrectionRequest request = DuplicateCorrectionRequest.builder()
    .date("2024-12-20")
    .build();
app.taxInvoices().issueDuplicateCorrection(originalKey, request);
```

---

## 오류 처리

```java
import io.bolta.exception.BoltaApiException;
import io.bolta.exception.BoltaException;

try {
    IssuanceKey key = app.taxInvoices().issue(invoice);
} catch (BoltaApiException e) {
    // API 오류 (서버 응답)
    System.err.println("상태 코드: " + e.getStatusCode());
    System.err.println("응답 내용: " + e.getResponseBody());
} catch (BoltaException e) {
    // SDK/네트워크 오류
    System.err.println("오류: " + e.getMessage());
}
```

---

## 고급 설정

### 재시도 설정

```java
RetryOption retryOption = RetryOption.builder()
    .maxAttempts(3)
    .exponentialBackoff(1000, 2.0, 30000)
    .enableJitter(0.2)
    .retryOnStatusCodes(RangeStatusCodeMatcher.of(500, 599))
    .retryOnNetworkError(true)
    .build();

RequestOptions options = RequestOptions.builder()
    .retryOption(retryOption)
    .build();

app.taxInvoices().issue(invoice, options);
```

### 커스텀 타임아웃

```java
BoltaConfig config = BoltaConfig.builder()
    .apiKey("live_your_api_key")
    .connectTimeoutMillis(10000)
    .readTimeoutMillis(60000)
    .writeTimeoutMillis(60000)
    .build();
```

---

## 도움이 필요하신가요?

- [Bolta API 문서](https://api-docs.bolta.io)
- [GitHub Issues](https://github.com/your-repo/bolta-java-sdk/issues)

**주의**: 이 SDK는 비공식입니다. 공식 Bolta 지원은 Bolta에 직접 문의하세요.
