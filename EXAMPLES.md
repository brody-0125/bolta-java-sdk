# Bolta Java SDK - Usage Examples

> **This is an UNOFFICIAL SDK** - Not affiliated with or endorsed by Bolta

This document provides practical usage examples for the Bolta Java SDK, organized by issuance type and use case.

**[한국어 버전](EXAMPLES_ko.md)**

## Table of Contents

- [Quick Start](#quick-start)
- [Standard Issuance](#standard-issuance)
  - [Receipt - Payment Received](#receipt---payment-received)
  - [Claim - Payment Requested](#claim---payment-requested)
- [Tax-Exempt and Zero-Rate Issuance](#tax-exempt-and-zero-rate-issuance)
  - [Tax-Exempt](#tax-exempt)
  - [Zero-Rate](#zero-rate)
- [Reverse Issuance](#reverse-issuance)
  - [When to Use](#when-to-use-reverse-issuance)
  - [Batch Processing Example](#batch-processing-example)
- [Amendment](#amendment)
- [Error Handling](#error-handling)
- [Advanced Configuration](#advanced-configuration)

---

## Quick Start

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

## Standard Issuance

Standard issuance is when the **supplier directly** issues a tax invoice. It is classified into **Receipt** and **Claim** depending on the purpose of issuance.

### Receipt - Payment Received

**Receipt** is a tax invoice issued **after payment has been received**.

**Processing Order:**
```
Transaction occurs → Payment confirmed → Request info → Receive info → Issue tax invoice
```

**When to use:**
- After cash/card payment is completed
- After end-of-month settlement is completed
- After prepayment (deposit, down payment) is confirmed

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

        // Supplier information
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("Supplier Corp.")
            .representativeName("John Kim")
            .address("123 Teheran-ro, Gangnam-gu, Seoul")
            .businessType("Wholesale/Retail")
            .businessItem("Electronics")
            .manager(Manager.builder()
                .name("Jane Lee")
                .email("manager@supplier.co.kr")
                .telephone("02-1234-5678")
                .build())
            .build();

        // Supplied party information
        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("Purchaser Inc.")
            .representativeName("Mike Park")
            .address("456 Seocho-daero, Seocho-gu, Seoul")
            .businessType("Manufacturing")
            .businessItem("Machinery")
            .managers(List.of(
                Manager.builder()
                    .name("Sarah Choi")
                    .email("buyer@purchaser.co.kr")
                    .telephone("02-9876-5432")
                    .build()
            ))
            .build();

        // Items
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("Laptop")
                .specification("15-inch, 16GB RAM")
                .quantity(10)
                .unitPrice(1500000L)
                .supplyCost(15000000L)    // 15,000,000 KRW
                .tax(1500000L)            // 1,500,000 KRW (10%)
                .build()
        );

        // Receipt tax invoice (payment received)
        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-15")
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("December delivery - Payment received")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("Receipt tax invoice issued: " + key.getValue());
        } catch (Exception e) {
            System.err.println("Issuance failed: " + e.getMessage());
        }
    }
}
```

---

### Claim - Payment Requested

**Claim** is a tax invoice issued **before payment is received**.

**Processing Order:**
```
Transaction occurs → Request info → Receive info → Issue tax invoice → Payment confirmed
```

**When to use:**
- Deferred payment transactions
- Billing after end-of-month closing
- Construction progress payment claims
- Project milestone billing

**Key difference**: Claim waits for payment after issuance; Receipt issues after payment.

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

        // Supplier/Supplied setup (same as above example)
        Supplier supplier = createSupplier();
        Supplied supplied = createSupplied();

        // Items: Service provision
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-31")
                .name("System Development Service")
                .specification("1st Milestone - Design Complete")
                .quantity(1)
                .unitPrice(50000000L)
                .supplyCost(50000000L)    // 50,000,000 KRW
                .tax(5000000L)            // 5,000,000 KRW
                .description("25% of 200M contract - Design completion")
                .build()
        );

        // Claim tax invoice (payment not yet received)
        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-31")
            .purpose(IssuancePurpose.CLAIM)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("System Development 1st Milestone Claim")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("Claim tax invoice issued: " + key.getValue());
        } catch (Exception e) {
            System.err.println("Issuance failed: " + e.getMessage());
        }
    }
}
```

---

## Tax-Exempt and Zero-Rate Issuance

### Tax-Exempt

**Tax-exempt** refers to transactions for goods/services that are **not subject to VAT**.

> **Important**: Tax-exempt businesses issue **"Invoice" (계산서)**, not "Tax Invoice" (세금계산서).
> Tax invoices include VAT, while invoices do not.

**Tax-exempt business types:**
- **Agricultural/Livestock/Fishery**: Unprocessed agricultural, livestock, fishery products
- **Medical**: Hospitals, clinics, oriental medicine clinics
- **Education Services**: Academies, tutoring centers
- **Publishing/News**: Books, newspapers, magazines
- **Welfare Services**: Childcare facilities, social welfare services

**Eligible issuers:**
- Businesses registered as tax-exempt
- Businesses with both taxable and tax-exempt items (for tax-exempt items only)

**Notes:**
- Tax-exempt businesses must still **receive tax invoices for purchases**
- Missing purchase tax invoices may result in expense disallowance
- Corporate businesses are required to issue electronic invoices

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

        // Supplier: Tax-exempt business (e.g., agricultural wholesaler)
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("Fresh Farm Wholesale")
            .representativeName("Kim Farmer")
            .address("123 Gyeongchun-ro, Gapyeong-gun, Gyeonggi-do")
            .businessType("Wholesale/Retail")
            .businessItem("Agricultural Products Wholesale")  // Tax-exempt item
            .manager(Manager.builder()
                .name("Lee Manager")
                .email("sales@farm.co.kr")
                .telephone("031-123-4567")
                .build())
            .build();

        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("Food Mart Inc.")
            .representativeName("Park Mart")
            .address("456 Ttukseom-ro, Seongdong-gu, Seoul")
            .businessType("Wholesale/Retail")
            .businessItem("General Retail")
            .build();

        // Items: Tax-exempt agricultural products (tax = 0)
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("Korean Apples")
                .specification("Fuji, 10kg box")
                .quantity(100)
                .unitPrice(30000L)
                .supplyCost(3000000L)     // 3,000,000 KRW
                .tax(0L)                  // Tax-exempt: 0 KRW
                .build(),
            TaxInvoiceLineItem.builder()
                .date("2024-12-15")
                .name("Korean Pears")
                .specification("Singo, 15kg box")
                .quantity(80)
                .unitPrice(45000L)
                .supplyCost(3600000L)     // 3,600,000 KRW
                .tax(0L)                  // Tax-exempt: 0 KRW
                .build()
        );

        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-15")
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("December agricultural products (Tax-exempt)")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("Tax-exempt invoice issued: " + key.getValue());
            System.out.println("Supply cost: 6,600,000 KRW, Tax: 0 KRW");
        } catch (Exception e) {
            System.err.println("Issuance failed: " + e.getMessage());
        }
    }
}
```

---

### Zero-Rate

**Zero-rate** is a **taxable transaction** under the VAT Act, but the tax rate is **0%**.

> **Key point**: No VAT is paid, but **input tax refund is possible**.
> Zero-rate transactions **require tax invoice issuance**.

**Zero-rate applicable items:**
- **Export-related**: Direct export, Local L/C, bonded area transactions
- **International transport**: Cross-border freight, foreign navigation vessels/aircraft
- **Overseas services**: Overseas construction, technical consulting
- **Strategic industry exports**: Semiconductors, secondary batteries, renewable energy, cultural content

**Eligible issuers:**
- Exporters (direct export, transit export)
- Foreign currency earning businesses
- International transport operators
- Local L/C transaction businesses

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

        // Supplier: Export company
        Supplier supplier = Supplier.builder()
            .identificationNumber("1234567890")
            .organizationName("GlobalTech Inc.")
            .representativeName("Kim Export")
            .address("123 Dongtan Industrial Rd, Hwaseong-si, Gyeonggi-do")
            .businessType("Manufacturing")
            .businessItem("Electronic Components Manufacturing and Export")
            .manager(Manager.builder()
                .name("Lee Trade")
                .email("export@globaltech.co.kr")
                .telephone("031-234-5678")
                .build())
            .build();

        // Supplied: Domestic intermediary (export agent)
        Supplied supplied = Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("Trade Agency Co.")
            .representativeName("Park Trade")
            .address("789 Teheran-ro, Gangnam-gu, Seoul")
            .businessType("Wholesale/Retail")
            .businessItem("Trade Intermediary")
            .build();

        // Items: Export goods (zero-rate applied)
        List<TaxInvoiceLineItem> items = List.of(
            TaxInvoiceLineItem.builder()
                .date("2024-12-20")
                .name("Semiconductor Wafer")
                .specification("300mm, for export")
                .quantity(1000)
                .unitPrice(500000L)
                .supplyCost(500000000L)   // 500,000,000 KRW
                .tax(0L)                  // Zero-rate: 0 KRW
                .description("Export to USA (B/L No: ABCD1234)")
                .build()
        );

        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-12-20")
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("Semiconductor export (Zero-rate)")
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("Zero-rate tax invoice issued: " + key.getValue());
            System.out.println("Supply cost: 500,000,000 KRW, Tax: 0 KRW (Zero-rate)");
        } catch (Exception e) {
            System.err.println("Issuance failed: " + e.getMessage());
        }
    }
}
```

**Tax-Exempt vs Zero-Rate Comparison:**

| Category | Tax-Exempt | Zero-Rate |
|----------|------------|-----------|
| Tax law nature | **Not subject to taxation** | **Taxable transaction** (rate is 0%) |
| Document issued | **Invoice** (Electronic Invoice) | **Tax Invoice** (Required) |
| Input tax deduction | **Not possible** | **Possible** (Refundable) |
| Target | Basic necessities, medical, education | Export, foreign currency earnings |

> **Tip**: Zero-rate is a policy benefit to promote exports, allowing businesses to receive refunds on VAT paid during purchases.

---

## Reverse Issuance

### When to Use Reverse Issuance

**Reverse issuance** is when the **supplied party** requests tax invoice issuance, and the **supplier** approves it.

**When to use:**
- When the supplier fails to issue or delays tax invoice issuance
- When large corporations centrally manage tax invoices from small/medium suppliers
- When regularly purchasing from multiple suppliers
- When processing large volumes of tax invoices efficiently through **batch processing**

**Process:**
1. Supplied party creates reverse issuance request
2. System sends approval URL to supplier
3. Supplier accesses URL and approves
4. Tax invoice is automatically issued

---

### Batch Processing Example (Spring Batch)

Example of a Spring Batch-based application where a large corporation's procurement team requests tax invoices from multiple suppliers at month-end.

#### 1. Batch Configuration (BatchConfig.java)

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

#### 2. Tasklet Implementation (ReverseIssuanceTasklet.java)

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
 * Month-end Reverse Issuance Batch Tasklet
 *
 * Execution: Last day of each month at 23:00 (scheduler configured)
 * Processing:
 * - Query pending purchase data from ERP
 * - Create reverse issuance requests per supplier
 * - Send approval URLs via email
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
        // 1. Query pending purchase data
        List<PurchaseOrder> pendingOrders = purchaseRepository
            .findByTaxInvoiceStatusAndClosingMonth("PENDING", getCurrentMonth());

        int successCount = 0;
        int failCount = 0;

        // 2. Process each reverse issuance request
        for (PurchaseOrder order : pendingOrders) {
            try {
                processReverseIssuance(order);
                successCount++;
                contribution.incrementReadCount();
            } catch (Exception e) {
                failCount++;
                contribution.incrementReadSkipCount();
                // Log failure and continue processing
                logFailure(order, e);
            }
        }

        // 3. Store results in ExecutionContext
        chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext()
            .put("processedCount", successCount + failCount);

        return RepeatStatus.FINISHED;
    }

    private void processReverseIssuance(PurchaseOrder order) {
        // Create reverse issuance request
        TaxInvoiceIssuanceRequest request = TaxInvoiceIssuanceRequest.builder()
            .date(order.getTransactionDate())
            .purpose(IssuancePurpose.RECEIPT)
            .supplier(buildSupplier(order.getSupplier()))
            .supplied(buildOurCompany())
            .items(buildLineItems(order.getItems()))
            .description(order.getDescription())
            .build();

        // Execute reverse issuance request
        IssuanceKey requestKey = boltaApp.taxInvoiceIssuanceRequests()
            .request(request);

        // Get approval URL
        String grantUrl = boltaApp.taxInvoiceIssuanceRequests()
            .getGrantUrl(requestKey.getValue());

        // Update DB status
        order.setTaxInvoiceStatus("REQUESTED");
        order.setIssuanceKey(requestKey.getValue());
        purchaseRepository.save(order);

        // Send approval request email to supplier
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
            .organizationName("Big Corporation Inc.")
            .representativeName("CEO Kim")
            .address("123 Yeouido-ro, Yeongdeungpo-gu, Seoul")
            .businessType("Manufacturing")
            .businessItem("Electronics Manufacturing")
            .managers(List.of(
                Manager.builder()
                    .name("Procurement Team")
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
        // Save failure log (separate table or logging system)
    }

    private String getCurrentMonth() {
        return java.time.YearMonth.now().toString();
    }
}
```

#### 3. BoltaApp Bean Configuration

```java
import io.bolta.BoltaApp;
import io.bolta.BoltaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoltaConfiguration {

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

#### 4. Scheduler Configuration

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

    // Run on the last day of each month at 23:00
    @Scheduled(cron = "0 0 23 L * ?")
    public void runMonthlyReverseIssuance() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(reverseIssuanceJob, params);
    }
}
```

**Cancel Reverse Issuance Request:**

```java
// Cancel request before supplier approves
app.taxInvoiceIssuanceRequests().cancel(requestKey);
```

---

## Amendment

For cases when an issued tax invoice needs to be modified.

| Reason | Method | When to use |
|--------|--------|-------------|
| Contract termination | `issueContractTermination()` | Contract cancellation |
| Supply cost change | `issueAmendSupplyCost()` | Returns, discounts |
| Duplicate correction | `issueDuplicateCorrection()` | Accidental duplicate issuance |
| Error correction | `issueErrorCorrection()` | Incorrect amount, item, etc. |

```java
// Contract termination
ContractTerminationRequest request = ContractTerminationRequest.builder()
    .date("2024-12-20")
    .build();
app.taxInvoices().issueContractTermination(originalKey, request);

// Duplicate correction
DuplicateCorrectionRequest request = DuplicateCorrectionRequest.builder()
    .date("2024-12-20")
    .build();
app.taxInvoices().issueDuplicateCorrection(originalKey, request);
```

---

## Error Handling

```java
import io.bolta.exception.BoltaApiException;
import io.bolta.exception.BoltaException;

try {
    IssuanceKey key = app.taxInvoices().issue(invoice);
} catch (BoltaApiException e) {
    // API error (server response)
    System.err.println("Status Code: " + e.getStatusCode());
    System.err.println("Response Body: " + e.getResponseBody());
} catch (BoltaException e) {
    // SDK/Network error
    System.err.println("Error: " + e.getMessage());
}
```

---

## Advanced Configuration

### Retry Configuration

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

### Custom Timeout

```java
BoltaConfig config = BoltaConfig.builder()
    .apiKey("live_your_api_key")
    .connectTimeoutMillis(10000)
    .readTimeoutMillis(60000)
    .writeTimeoutMillis(60000)
    .build();
```

---

## Need Help?

- [Bolta API Documentation](https://api-docs.bolta.io)
- [GitHub Issues](https://github.com/your-repo/bolta-java-sdk/issues)

**Reminder**: This is an unofficial SDK. For official Bolta support, contact Bolta directly.
