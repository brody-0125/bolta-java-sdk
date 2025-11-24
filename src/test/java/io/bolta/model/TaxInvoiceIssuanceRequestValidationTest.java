package io.bolta.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TaxInvoiceIssuanceRequest} validation requirements.
 */
class TaxInvoiceIssuanceRequestValidationTest {

        @Test
        void testTaxInvoiceIssuanceRequestCreation_Success() {
                // Arrange & Act
                TaxInvoiceIssuanceRequest request = TaxInvoiceIssuanceRequest.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .description("Test Description")
                                .build();

                // Assert
                assertNotNull(request);
                assertEquals("2024-01-01", request.getDate());
                assertEquals(IssuancePurpose.RECEIPT, request.getPurpose());
                assertNotNull(request.getSupplier());
                assertNotNull(request.getSupplied());
                assertNotNull(request.getTaxInvoiceLineItems());
                assertEquals(1, request.getTaxInvoiceLineItems().size());
                assertEquals("Test Description", request.getDescription());
        }

        @Test
        void testTaxInvoiceIssuanceRequestCreation_MissingTopLevelFields() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> TaxInvoiceIssuanceRequest.builder()
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when date is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoiceIssuanceRequest.builder()
                                .date("2024-01-01")
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when purpose is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoiceIssuanceRequest.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when supplier is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoiceIssuanceRequest.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when supplied is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoiceIssuanceRequest.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .build(), "Should throw NPE when items is missing");
        }

        // Helper methods to create valid objects
        private Supplier createValidSupplier() {
                return Supplier.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Supplier Org")
                                .representativeName("Supplier Rep")
                                .manager(createValidManager())
                                .build();
        }

        private Supplied createValidSupplied() {
                return Supplied.builder()
                                .identificationNumber("0987654321")
                                .organizationName("Supplied Org")
                                .representativeName("Supplied Rep")
                                .build();
        }

        private TaxInvoiceLineItem createValidTaxInvoiceLineItem() {
                return TaxInvoiceLineItem.builder()
                                .date("2024-01-01")
                                .name("TaxInvoiceLineItem Name")
                                .supplyCost(10000L)
                                .build();
        }

        private Manager createValidManager() {
                return Manager.builder()
                                .email("manager@example.com")
                                .build();
        }
}
