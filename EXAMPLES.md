# Bolta Java SDK - Usage Examples

> ⚠️ **This is an UNOFFICIAL SDK** - Not affiliated with or endorsed by Bolta

This document provides comprehensive usage examples for the Bolta Java SDK.

## Table of Contents

- [Basic Setup](#basic-setup)
- [E-Tax Invoice Operations](#e-tax-invoice-operations)
  - [Standard Issuance (정발행)](#standard-issuance-정발행)
  - [Query Invoice (세금계산서 조회)](#query-invoice-세금계산서-조회)
  - [Async Operations](#async-operations)
- [Customer Management](#customer-management)
- [Multi-Customer Platform](#multi-customer-platform)
- [Error Handling](#error-handling)
- [Advanced Configuration](#advanced-configuration)

## Basic Setup

### Simple Initialization

```java
import io.bolta.BoltaApp;

public class BoltaExample {
    public static void main(String[] args) {
        // Initialize with API key
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key-here")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();
    }
}
```

### Advanced Configuration

```java
import io.bolta.BoltaApp;
import io.bolta.BoltaConfig;

BoltaConfig config = BoltaConfig.builder()
    .apiKey("your-api-key-here")
    .baseUrl("https://xapi.bolta.io")
    .connectTimeoutMillis(15000)  // 15 seconds
    .readTimeoutMillis(60000)     // 60 seconds
    .writeTimeoutMillis(60000)    // 60 seconds
    .build();

BoltaApp app = BoltaApp.builder()
    .config(config)
    .build();
```

## E-Tax Invoice Operations

### Standard Issuance (정발행)

#### Complete Example

```java
import io.bolta.model.*;
import java.util.List;

public class IssuanceExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // Build supplier information (공급자)
        TaxInvoice.Supplier supplier = TaxInvoice.Supplier.builder()
            .identificationNumber("1234567890")
            .taxRegistrationId("0001")  // Optional for branches
            .organizationName("테크 주식회사")
            .representativeName("김대표")
            .managerEmail("manager@techcorp.com")
            .address("서울특별시 강남구 테헤란로 123")
            .businessItem("제조업")
            .businessType("전자제품")
            .build();

        // Build supplied party information (공급받는자)
        TaxInvoice.Supplied supplied = TaxInvoice.Supplied.builder()
            .identificationNumber("0987654321")
            .organizationName("소매 상사")
            .representativeName("이사장")
            .address("서울특별시 서초구 서초대로 456")
            .businessItem("도소매업")
            .businessType("전자제품 판매")
            .email1("billing@retailco.com")
            .email2("accounting@retailco.com")  // Optional
            .build();

        // Build invoice items (품목)
        List<TaxInvoice.Item> items = List.of(
            TaxInvoice.Item.builder()
                .date("2024-01-15")
                .name("스마트폰")
                .quantity(10)
                .unitPrice(500000L)
                .supplyCost(5000000L)  // quantity * unitPrice
                .tax(500000L)          // 10% VAT
                .specification("64GB")
                .description("최신 모델")
                .build(),
            TaxInvoice.Item.builder()
                .date("2024-01-15")
                .name("노트북")
                .quantity(5)
                .unitPrice(1000000L)
                .supplyCost(5000000L)
                .tax(500000L)
                .specification("16GB RAM")
                .build()
        );

        // Build the complete invoice
        TaxInvoice invoice = TaxInvoice.builder()
            .date("2024-01-15")
            .purpose(IssuancePurpose.RECEIPT)  // 영수
            .supplier(supplier)
            .supplied(supplied)
            .items(items)
            .description("1월 정기 납품분")
            .build();

        try {
            // Issue the invoice
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("✅ Invoice issued successfully!");
            System.out.println("Issuance Key: " + key.getValue());
        } catch (Exception e) {
            System.err.println("❌ Failed to issue invoice: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

#### Claim Invoice (청구)

```java
TaxInvoice invoice = TaxInvoice.builder()
    .date("2024-01-20")
    .purpose(IssuancePurpose.CLAIM)  // 청구 - before payment
    .supplier(supplier)
    .supplied(supplied)
    .items(items)
    .description("2월 예정 납품분 청구")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice);
```

### Query Invoice (세금계산서 조회)

```java
import io.bolta.model.TaxInvoice;

public class QueryExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        String issuanceKey = "8D529FAD3EBAE050B79CE943CCC7CEDE";

        try {
            TaxInvoice invoice = app.taxInvoices().get(issuanceKey);
            
            System.out.println("Invoice Date: " + invoice.getDate());
            System.out.println("Purpose: " + invoice.getPurpose());
            
            // Supplier info
            System.out.println("\n=== Supplier (공급자) ===");
            System.out.println("Name: " + invoice.getSupplier().getOrganizationName());
            System.out.println("Representative: " + invoice.getSupplier().getRepresentativeName());
            
            // Supplied party info
            System.out.println("\n=== Supplied Party (공급받는자) ===");
            System.out.println("Name: " + invoice.getSupplied().getOrganizationName());
            System.out.println("Email: " + invoice.getSupplied().getEmail1());
            
            // Items
            System.out.println("\n=== Items (품목) ===");
            invoice.getItems().forEach(item -> {
                System.out.printf("%s x%d = %,d원 (세액: %,d원)%n",
                    item.getName(),
                    item.getQuantity(),
                    item.getSupplyCost(),
                    item.getTax());
            });
            
        } catch (Exception e) {
            System.err.println("Failed to retrieve invoice: " + e.getMessage());
        }
    }
}
```

### Async Operations

```java
import java.util.concurrent.CompletableFuture;

public class AsyncExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        // Async issuance
        CompletableFuture<IssuanceKey> issueFuture = 
            app.taxInvoices().issueAsync(invoice);

        issueFuture
            .thenAccept(key -> {
                System.out.println("✅ Issued: " + key.getValue());
                
                // Chain async query
                return app.taxInvoices().getAsync(key.getValue());
            })
            .thenAccept(retrievedInvoice -> {
                System.out.println("✅ Retrieved: " + 
                    retrievedInvoice.getSupplier().getOrganizationName());
            })
            .exceptionally(error -> {
                System.err.println("❌ Error: " + error.getMessage());
                return null;
            });

        // Keep main thread alive for async operations
        Thread.sleep(5000);
    }
}
```

## Customer Management

### Create Customer (고객 생성)

```java
import io.bolta.model.Customer;

public class CustomerExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        Customer customer = Customer.builder()
            .identificationNumber("1234567890")
            .taxRegistrationId("0001")  // Optional
            .organizationName("신규 고객사")
            .representativeName("박대표")
            .address("경기도 성남시 분당구 판교역로 123")
            .businessItem("IT서비스업")
            .businessType("소프트웨어 개발")
            .email1("admin@newcustomer.com")
            .email2("billing@newcustomer.com")
            .build();

        try {
            app.customers().create(customer);
            System.out.println("✅ Customer created successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to create customer: " + e.getMessage());
        }
    }
}
```

### Retrieve Customer (고객 조회)

```java
Customer customer = app.customers().get("1234567890");
System.out.println("Customer: " + customer.getOrganizationName());
System.out.println("Email: " + customer.getEmail1());
```

### Delete Customer Certificate (고객 공동인증서 등록해제)

```java
app.customers().delete("1234567890");
System.out.println("✅ Customer certificate unregistered");
```

## Multi-Customer Platform

For SaaS platforms managing multiple customers:

```java
import io.bolta.model.RequestOptions;

public class PlatformExample {
    private final BoltaApp app;

    public PlatformExample() {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("platform-api-key")
            .build();

        this.app = BoltaApp.builder()
            .config(config)
            .build();
    }

    public void issueForCustomer(String customerKey, TaxInvoice invoice) {
        // Specify which customer this operation is for
        RequestOptions options = RequestOptions.builder()
            .customerKey(customerKey)
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice, options);
            System.out.println("✅ Issued for customer " + customerKey);
            System.out.println("Key: " + key.getValue());
        } catch (Exception e) {
            System.err.println("❌ Failed for customer " + customerKey);
        }
    }

    public static void main(String[] args) {
        PlatformExample platform = new PlatformExample();
        
        // Issue invoices for different customers
        platform.issueForCustomer("customer-001", invoice1);
        platform.issueForCustomer("customer-002", invoice2);
    }
}
```

## Error Handling

### Comprehensive Error Handling

```java
import io.bolta.exception.BoltaApiException;
import io.bolta.exception.BoltaException;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey("your-api-key")
            .build();

        BoltaApp app = BoltaApp.builder()
            .config(config)
            .build();

        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            System.out.println("Success: " + key.getValue());
            
        } catch (BoltaApiException e) {
            // API returned an error response
            System.err.println("🔴 API Error");
            System.err.println("Status Code: " + e.getStatusCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Response Body: " + e.getResponseBody());
            
            // Handle specific error codes
            if (e.getStatusCode() == 400) {
                System.err.println("Invalid request data");
            } else if (e.getStatusCode() == 401) {
                System.err.println("Authentication failed - check API key");
            } else if (e.getStatusCode() == 404) {
                System.err.println("Resource not found");
            }
            
        } catch (BoltaException e) {
            // Network or SDK error
            System.err.println("🔴 SDK Error");
            System.err.println("Message: " + e.getMessage());
            
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            
        } catch (Exception e) {
            // Unexpected error
            System.err.println("🔴 Unexpected Error");
            e.printStackTrace();
        }
    }
}
```

## Advanced Configuration

### Custom HTTP Client

```java
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

OkHttpClient customHttpClient = new OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build();

BoltaClient client = BoltaClient.builder()
    .config(config)
    .httpClient(customHttpClient)
    .build();

BoltaApp app = new BoltaApp(client);
```

### Logging and Monitoring

```java
public class MonitoredBoltaClient {
    private final BoltaApp app;
    
    public MonitoredBoltaClient(String apiKey) {
        BoltaConfig config = BoltaConfig.builder()
            .apiKey(apiKey)
            .build();

        this.app = BoltaApp.builder()
            .config(config)
            .build();
    }
    
    public IssuanceKey issueWithLogging(TaxInvoice invoice) {
        long startTime = System.currentTimeMillis();
        
        try {
            IssuanceKey key = app.taxInvoices().issue(invoice);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.printf("✅ Invoice issued in %dms: %s%n", 
                duration, key.getValue());
            
            return key;
            
        } catch (BoltaApiException e) {
            long duration = System.currentTimeMillis() - startTime;
            
            System.err.printf("❌ API error after %dms: [%d] %s%n",
                duration, e.getStatusCode(), e.getMessage());
            
            throw e;
        }
    }
}
```

---

## Need Help?

- 📖 [Bolta API Documentation](https://api-docs.bolta.io)
- 📧 [GitHub Issues](https://github.com/yourusername/bolta-java-sdk/issues)

**Reminder**: This is an unofficial SDK. For official Bolta support, contact Bolta directly.
