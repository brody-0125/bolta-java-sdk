package io.bolta.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TaxInvoice} validation requirements.
 * <p>
 * This test class serves as documentation for the mandatory fields of a
 * TaxInvoice.
 */
class TaxInvoiceValidationTest {

        @Test
        void testTaxInvoiceCreation_Success() {
                // Arrange & Act
                TaxInvoice invoice = TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build();

                // Assert
                assertNotNull(invoice);
        }

        @Test
        void testTaxInvoiceCreation_MissingTopLevelFields() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> TaxInvoice.builder()
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when date is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoice.builder()
                                .date("2024-01-01")
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when purpose is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplied(createValidSupplied())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when supplier is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .items(Collections.singletonList(createValidTaxInvoiceLineItem()))
                                .build(), "Should throw NPE when supplied is missing");

                assertThrows(NullPointerException.class, () -> TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(createValidSupplier())
                                .supplied(createValidSupplied())
                                .build(), "Should throw NPE when items is missing");
        }

        @Test
        void testSupplierValidation() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> Supplier.builder()
                                .organizationName("Org Name")
                                .representativeName("Rep Name")
                                .manager(createValidManager())
                                .build());

                assertThrows(NullPointerException.class, () -> Supplier.builder()
                                .identificationNumber("1234567890")
                                .representativeName("Rep Name")
                                .manager(createValidManager())
                                .build());

                assertThrows(NullPointerException.class, () -> Supplier.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Org Name")
                                .manager(createValidManager())
                                .build());

                assertThrows(NullPointerException.class, () -> Supplier.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Org Name")
                                .representativeName("Rep Name")
                                .build());
        }

        @Test
        void testSuppliedValidation() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> Supplied.builder()
                                .organizationName("Org Name")
                                .representativeName("Rep Name")
                                .build());

                assertThrows(NullPointerException.class, () -> Supplied.builder()
                                .identificationNumber("1234567890")
                                .representativeName("Rep Name")
                                .build());

                assertThrows(NullPointerException.class, () -> Supplied.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Org Name")
                                .build());
        }

        @Test
        void testTaxInvoiceLineItemValidation() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> TaxInvoiceLineItem.builder()
                                .name("TaxInvoiceLineItem Name")
                                .supplyCost(1000L)
                                .build());

                assertThrows(NullPointerException.class, () -> TaxInvoiceLineItem.builder()
                                .date("2024-01-01")
                                .supplyCost(1000L)
                                .build());

                assertThrows(NullPointerException.class, () -> TaxInvoiceLineItem.builder()
                                .date("2024-01-01")
                                .name("TaxInvoiceLineItem Name")
                                .build());
        }

        @Test
        void testManagerValidation() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> Manager.builder()
                                .name("Manager Name")
                                .telephone("010-1234-5678")
                                .build());
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
