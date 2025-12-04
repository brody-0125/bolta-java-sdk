package io.bolta;

import io.bolta.model.IssuanceKey;
import io.bolta.model.IssuancePurpose;
import io.bolta.model.TaxInvoiceLineItem;
import io.bolta.model.Manager;
import io.bolta.model.Supplied;
import io.bolta.model.Supplier;
import io.bolta.model.TaxInvoiceIssuanceRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaxInvoiceReverseIssuanceTest extends ClientTestSupport {

        @Test
        void testReverseIssuanceRequest() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"REVERSE_KEY_1\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                TaxInvoiceIssuanceRequest request = TaxInvoiceIssuanceRequest.builder()
                                .date("2024-03-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(Supplier.builder()
                                                .identificationNumber("1234567890")
                                                .organizationName("Supplier Corp")
                                                .representativeName("Supplier Rep")
                                                .manager(Manager.builder()
                                                                .email("supplier@example.com")
                                                                .build())
                                                .build())
                                .supplied(Supplied.builder()
                                                .identificationNumber("0987654321")
                                                .organizationName("Supplied Corp")
                                                .representativeName("Supplied Rep")
                                                .build())
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-03-01")
                                                .name("Reverse TaxInvoiceLineItem")
                                                .supplyCost(10000L)
                                                .build()))
                                .build();

                // Act
                IssuanceKey key = app.taxInvoiceIssuanceRequests().request(request);

                // Assert
                assertNotNull(key);
                assertEquals("REVERSE_KEY_1", key.getValue());

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/issueRequest", recordedRequest.getPath());
                assertEquals("POST", recordedRequest.getMethod());
        }

        @Test
        void testGetGrantUrl() throws Exception {
                // Arrange
                String jsonResponse = "{\"url\": \"https://bolta.io/grant/123\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                String url = app.taxInvoiceIssuanceRequests().getGrantUrl("REVERSE_KEY_1");

                // Assert
                assertEquals("https://bolta.io/grant/123", url);

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/REVERSE_KEY_1/issueRequest/grant", recordedRequest.getPath());
                assertEquals("GET", recordedRequest.getMethod());
        }

        @Test
        void testCancelReverseIssuance() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"CANCELLED_KEY_1\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                IssuanceKey key = app.taxInvoiceIssuanceRequests().cancel("REVERSE_KEY_1");

                // Assert
                assertNotNull(key);
                assertEquals("CANCELLED_KEY_1", key.getValue());

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/REVERSE_KEY_1/issueRequest/cancel", recordedRequest.getPath());
                assertEquals("PUT", recordedRequest.getMethod());
        }

        @Test
        void testReverseIssuanceRequestWithOptions() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"REVERSE_KEY_OPT\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                TaxInvoiceIssuanceRequest request = TaxInvoiceIssuanceRequest.builder()
                                .date("2024-03-01")
                                .purpose(IssuancePurpose.RECEIPT)
                                .supplier(Supplier.builder()
                                                .identificationNumber("1234567890")
                                                .organizationName("Supplier Corp")
                                                .representativeName("Supplier Rep")
                                                .manager(Manager.builder()
                                                                .email("supplier@example.com")
                                                                .build())
                                                .build())
                                .supplied(Supplied.builder()
                                                .identificationNumber("0987654321")
                                                .organizationName("Supplied Corp")
                                                .representativeName("Supplied Rep")
                                                .build())
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-03-01")
                                                .name("Item")
                                                .supplyCost(1000L)
                                                .build()))
                                .build();

                io.bolta.model.TaxInvoiceIssuanceRequestOptions options = io.bolta.model.TaxInvoiceIssuanceRequestOptions
                                .builder()
                                .customerKey("cust_key_123")
                                .clientReferenceId("ref_id_456")
                                .build();

                // Act
                IssuanceKey key = app.taxInvoiceIssuanceRequests().request(request, options);

                // Assert
                assertNotNull(key);
                assertEquals("REVERSE_KEY_OPT", key.getValue());

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/issueRequest", recordedRequest.getPath());
                // Note: Headers are NOT sent for TaxInvoiceIssuanceRequestResource as per
                // previous requirement,
                // but the options are passed for potential retry configuration.
                // If headers WERE supposed to be sent, we would assert them here.
                // Based on Step 72, headers are NOT added in TaxInvoiceIssuanceRequestResource.
        }
}
