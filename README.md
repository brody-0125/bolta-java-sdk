# Bolta Java SDK (Unofficial)

**[한국어](README.ko.md)** | **English**

> ⚠️ **This is an UNOFFICIAL SDK** for the Bolta e-tax invoice API. This SDK is not officially maintained or endorsed by Bolta. Use at your own risk.

A developer-friendly Java SDK for integrating with the [Bolta](https://bolta.io) e-tax invoice API.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Requirements

- Java 8 or higher
- Gradle or Maven

## Installation

### Gradle

Add to your `build.gradle` or `build.gradle.kts`:

```kotlin
dependencies {
    implementation 'io.bolta:bolta-java-sdk:0.0.1'
}
```

### Maven

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>io.bolta</groupId>
    <artifactId>bolta-java-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Quick Start

### Initialize the SDK

```java
import io.bolta.BoltaApp;

import io.bolta.BoltaApp;
import io.bolta.BoltaConfig;

BoltaConfig config = BoltaConfig.builder()
    .apiKey("your-api-key-here")
    .build();

BoltaApp app = BoltaApp.builder()
    .config(config)
    .build();
```

### Issue E-Tax Invoice

```java
import io.bolta.model.*;
import java.util.List;

TaxInvoice invoice = TaxInvoice.builder()
    .date("2024-01-15")
    .purpose(IssuancePurpose.RECEIPT)
    .supplier(Supplier.builder()
        .identificationNumber("1234567890")
        .organizationName("Supplier Corp")
        .representativeName("John Doe")
        .manager(Manager.builder()
            .email("manager@supplier.com")
            .name("Manager Name")
            .telephone("02-1234-5678")
            .build())
        .address("123 Business St, Seoul")
        .businessItem("Manufacturing")
        .businessType("Electronics")
        .build())
    .supplied(Supplied.builder()
        .identificationNumber("0987654321")
        .organizationName("Customer Corp")
        .representativeName("Jane Smith")
        .address("456 Commerce Rd, Seoul")
        .businessItem("Retail")
        .businessType("Electronics Sales")
        .managers(List.of(
            Manager.builder()
                .email("billing@customer.com")
                .name("Billing Manager")
                .telephone("02-9876-5432")
                .build()
        ))
        .build())
    .items(List.of(
        TaxInvoiceLineItem.builder()
            .date("2024-01-15")
            .name("Product Name")
            .quantity(10)
            .unitPrice(100000L)
            .supplyCost(1000000L)
            .tax(100000L)
            .build()
    ))
    .description("Monthly delivery")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice);
System.out.println("Issued invoice: " + key.getValue());
```

### Query E-Tax Invoice

```java
TaxInvoice invoice = app.taxInvoices().get("ISSUANCE_KEY_HERE");
System.out.println("Supplier: " + invoice.getSupplier().getOrganizationName());
```

### Revision (Modified Issuance) - 수정발행

Issue a revised invoice to correct errors or handle duplicate issuance mistakes:

#### Correction for Errors (기재사항 오류 정정)

```java
import io.bolta.model.RevisionType;

// Create the corrected invoice data
TaxInvoice correctedInvoice = TaxInvoice.builder()
    .date("2024-01-15")
    .purpose(IssuancePurpose.RECEIPT)
    .supplier(supplier)
    .supplied(supplied)
    .items(correctedItems)  // Corrected items
    .description("Corrected invoice")
    .build();

// Issue revision with the original invoice key
IssuanceKey newKey = app.taxInvoices().revise(
    "ORIGINAL_ISSUANCE_KEY",
    RevisionType.CORRECTION,
    correctedInvoice
);
```

#### Mistaken Duplicate Issuance (착오에 의한 이중발급)

```java
// Issue revision to cancel a duplicate invoice
IssuanceKey newKey = app.taxInvoices().revise(
    "DUPLICATE_INVOICE_KEY",
    RevisionType.DUPLICATE_MISTAKE,
    correctedInvoice
);
```

### Customer Management

```java
// Create customer
Customer customer = Customer.builder()
    .identificationNumber("1234567890")
    .organizationName("Customer Company")
    .representativeName("Representative Name")
    .address("Business Address")
    .businessItem("Business Item")
    .businessType("Business Type")
    .email1("customer@example.com")
    .build();

app.customers().create(customer);

// Retrieve customer
Customer retrieved = app.customers().get("1234567890");

// Unregister customer certificate
app.customers().delete("1234567890");
```

### Multi-Customer Platform

For platforms managing multiple customers:

```java
RequestOptions options = RequestOptions.builder()
    .customerKey("customer-specific-key")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

### Request Tracking with Client Reference ID

Track requests with your own reference identifier:

```java
RequestOptions options = RequestOptions.builder()
    .clientReferenceId("ABC_123")  // Your unique reference ID
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

Combine with customer key for platform scenarios:

```java
RequestOptions options = RequestOptions.builder()
    .customerKey("customer-001")
    .clientReferenceId("ORDER_2024_001")
    .build();

IssuanceKey key = app.taxInvoices().issue(invoice, options);
```

### Async Operations

```java
import java.util.concurrent.CompletableFuture;

CompletableFuture<IssuanceKey> future = app.taxInvoices().issueAsync(invoice);

future.thenAccept(key -> {
    System.out.println("Issued: " + key.getValue());
}).exceptionally(error -> {
    error.printStackTrace();
    return null;
});
```

## Error Handling

```java
import io.bolta.exception.*;

try {
    IssuanceKey key = app.taxInvoices().issue(invoice);
} catch (BoltaApiException e) {
    System.err.println("API Error: " + e.getMessage());
    System.err.println("Status Code: " + e.getStatusCode());
    System.err.println("Response Body: " + e.getResponseBody());
} catch (BoltaException e) {
    System.err.println("SDK Error: " + e.getMessage());
}
```

## Advanced Configuration

```java
BoltaConfig config = BoltaConfig.builder()
    .apiKey("your-api-key")
    .baseUrl("https://xapi.bolta.io")
    .connectTimeoutMillis(10000)
    .readTimeoutMillis(30000)
    .writeTimeoutMillis(30000)
    .build();

BoltaApp app = BoltaApp.builder()
    .config(config)
    .build();
```

## Documentation

- 📖 [Bolta API Documentation](https://api-docs.bolta.io)
- 📚 [Usage Examples](EXAMPLES.md)

## Building from Source

```bash
git clone https://github.com/yourusername/bolta-java-sdk.git
cd bolta-java-sdk
./gradlew build
```

## Testing

```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Disclaimer

⚠️ **IMPORTANT**: This is an **unofficial SDK** and is not affiliated with, maintained by, or endorsed by Bolta.

- This SDK is provided "as is" without warranty of any kind
- Use at your own risk in production environments
- For official support, please contact Bolta directly

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

- 📖 [API Documentation](https://api-docs.bolta.io)
- 📧 SDK Issues: [GitHub Issues](https://github.com/yourusername/bolta-java-sdk/issues)
- 🌐 Bolta API Support: Contact Bolta directly

---

**Note**: Bolta and all related trademarks are property of their respective owners.
