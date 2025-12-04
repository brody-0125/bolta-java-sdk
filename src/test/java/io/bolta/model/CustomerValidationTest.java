package io.bolta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Customer} validation requirements.
 * <p>
 * This test class serves as documentation for the mandatory fields of a
 * Customer.
 */
class CustomerValidationTest {

        @Test
        void testCustomerCreation_Success() {
                // Arrange & Act
                Customer customer = Customer.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Test Org")
                                .representativeName("Test Rep")
                                .build();

                // Assert
                assertNotNull(customer);
                assertEquals("1234567890", customer.getIdentificationNumber());
                assertEquals("Test Org", customer.getOrganizationName());
                assertEquals("Test Rep", customer.getRepresentativeName());
        }

        @Test
        void testCustomerCreation_MissingMandatoryFields() {
                // Arrange, Act & Assert
                assertThrows(NullPointerException.class, () -> Customer.builder()
                                .organizationName("Test Org")
                                .representativeName("Test Rep")
                                .build(), "Should throw NPE when identificationNumber is missing");

                assertThrows(NullPointerException.class, () -> Customer.builder()
                                .identificationNumber("1234567890")
                                .representativeName("Test Rep")
                                .build(), "Should throw NPE when organizationName is missing");

                assertThrows(NullPointerException.class, () -> Customer.builder()
                                .identificationNumber("1234567890")
                                .organizationName("Test Org")
                                .build(), "Should throw NPE when representativeName is missing");
        }
}
