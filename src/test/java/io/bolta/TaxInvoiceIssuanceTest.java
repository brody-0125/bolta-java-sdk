package io.bolta;

import io.bolta.model.IssuanceKey;
import io.bolta.model.IssuancePurpose;
import io.bolta.model.TaxInvoiceLineItem;
import io.bolta.model.Manager;
import io.bolta.model.Supplied;
import io.bolta.model.Supplier;
import io.bolta.model.TaxInvoice;
import io.bolta.model.TaxInvoiceIssuanceRequestOptions; // Changed from RequestOptions
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaxInvoiceIssuanceTest extends ClientTestSupport {

        @Test
        void testIssueTaxInvoice() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"8D529FAD3EBAE050B79CE943CCC7CEDE\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                TaxInvoice invoice = TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(Supplier.builder()
                                                .identificationNumber("1234567890")
                                                .organizationName("My Company")
                                                .representativeName("John Doe")
                                                .manager(Manager.builder()
                                                                .email("manager@example.com")
                                                                .build())
                                                .build())
                                .supplied(Supplied.builder()
                                                .identificationNumber("0987654321")
                                                .organizationName("Your Company")
                                                .representativeName("Jane Doe")
                                                .build())
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-01-01")
                                                .name("Service Fee")
                                                .supplyCost(10000L)
                                                .build()))
                                .build();

                TaxInvoiceIssuanceRequestOptions options = TaxInvoiceIssuanceRequestOptions.builder() // Changed from
                                                                                                      // RequestOptions
                                .customerKey("my-idempotency-key")
                                .build();

                // Act
                IssuanceKey key = app.taxInvoices().issue(invoice, options);

                // Assert
                assertNotNull(key);
                assertEquals("8D529FAD3EBAE050B79CE943CCC7CEDE", key.getValue());

                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/issue", request.getPath());
                assertEquals("POST", request.getMethod());
                assertEquals("Basic dGVzdF9hcGlfa2V5Og==", request.getHeader("Authorization"));
                assertEquals("my-idempotency-key", request.getHeader("Customer-Key"));
        }

        @Test
        void testGetTaxInvoice() throws Exception {
                // Arrange
                String jsonResponse = "{\"date\":\"2024-01-01\",\"purpose\":\"RECEIPT\",\"supplier\":{\"identificationNumber\":\"1234567890\",\"organizationName\":\"My Company\",\"representativeName\":\"John Doe\",\"manager\":{\"email\":\"manager@example.com\",\"name\":null,\"telephone\":null}},\"supplied\":{\"identificationNumber\":\"0987654321\",\"organizationName\":\"Your Company\",\"representativeName\":\"Jane Doe\",\"managers\":null},\"items\":[{\"date\":\"2024-01-01\",\"name\":\"Service Fee\",\"supplyCost\":10000}]}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                TaxInvoice invoice = app.taxInvoices().get("KEY123");

                // Assert
                assertNotNull(invoice);
                assertEquals("My Company", invoice.getSupplier().getOrganizationName());

                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/KEY123", request.getPath());
                assertEquals("GET", request.getMethod());
        }

        @Test
        void testApiError400BadRequest() throws Exception {
                // Arrange
                String errorResponse = "{\"error\":\"Invalid tax invoice data\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(400)
                                .setBody(errorResponse));

                TaxInvoice invoice = TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(Supplier.builder()
                                                .identificationNumber("1234567890")
                                                .organizationName("Test Corp")
                                                .representativeName("Test")
                                                .manager(Manager.builder()
                                                                .email("test@test.com")
                                                                .build())
                                                .build())
                                .supplied(Supplied.builder()
                                                .identificationNumber("0987654321")
                                                .organizationName("Client Corp")
                                                .representativeName("Client")
                                                .build())
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-01-01")
                                                .name("Service")
                                                .supplyCost(10000L)
                                                .build()))
                                .build();

                // Act & Assert
                try {
                        app.taxInvoices().issue(invoice);
                        org.junit.jupiter.api.Assertions.fail("Expected BoltaApiException");
                } catch (io.bolta.exception.BoltaApiException e) {
                        assertEquals(400, e.getStatusCode());
                        assertEquals(errorResponse, e.getResponseBody());
                        org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("API request failed"));
                }
        }

        @Test
        void testApiError404NotFound() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(404)
                                .setBody("{\"error\":\"Invoice not found\"}"));

                // Act & Assert
                try {
                        app.taxInvoices().get("NONEXISTENT_KEY");
                        org.junit.jupiter.api.Assertions.fail("Expected BoltaApiException");
                } catch (io.bolta.exception.BoltaApiException e) {
                        assertEquals(404, e.getStatusCode());
                }
        }

        @Test
        void testInvalidJsonResponse() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(200)
                                .setBody("This is not valid JSON"));

                // Act & Assert
                try {
                        app.taxInvoices().get("SOME_KEY");
                        org.junit.jupiter.api.Assertions.fail("Expected BoltaException");
                } catch (io.bolta.exception.BoltaException e) {
                        org.junit.jupiter.api.Assertions.assertTrue(
                                        e.getMessage().contains("Failed to parse response") ||
                                                        e.getMessage().contains("Network error"));
                }
        }

        @Test
        void testRequestOptionsWithClientReferenceId() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(200)
                                .setBody("{\"issuanceKey\":\"TEST_KEY\"}"));

                TaxInvoice invoice = TaxInvoice.builder()
                                .date("2024-01-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(Supplier.builder()
                                                .identificationNumber("1234567890")
                                                .organizationName("Test Corp")
                                                .representativeName("Test")
                                                .manager(Manager.builder()
                                                                .email("test@test.com")
                                                                .build())
                                                .build())
                                .supplied(Supplied.builder()
                                                .identificationNumber("0987654321")
                                                .organizationName("Client Corp")
                                                .representativeName("Client")
                                                .build())
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-01-01")
                                                .name("Service")
                                                .supplyCost(10000L)
                                                .build()))
                                .build();

                TaxInvoiceIssuanceRequestOptions options = TaxInvoiceIssuanceRequestOptions.builder()
                                .customerKey("customer-123")
                                .clientReferenceId("REF_ABC_123")
                                .build();

                // Act
                app.taxInvoices().issue(invoice, options);

                // Assert
                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("customer-123", request.getHeader("Customer-Key"));
                assertEquals("REF_ABC_123", request.getHeader("Bolta-Client-Reference-Id"));
        }
}
