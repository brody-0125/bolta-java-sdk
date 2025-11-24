package io.bolta;

import io.bolta.model.ContractTerminationRequest;
import io.bolta.model.IssuanceKey;
import io.bolta.model.TaxInvoiceLineItem;
import io.bolta.model.SupplyCostChangeRequest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaxInvoiceModifiedIssuanceTest extends ClientTestSupport {

        @Test
        void testIssueModifiedContractCancellation() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"MODIFIED_KEY_1\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                ContractTerminationRequest request = ContractTerminationRequest.builder()
                                .date("2024-02-01")
                                .build();

                // Act
                IssuanceKey key = app.taxInvoices().issueContractTermination("ORIGINAL_KEY", request);

                // Assert
                assertNotNull(key);
                assertEquals("MODIFIED_KEY_1", key.getValue());

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/ORIGINAL_KEY/amend/termination", recordedRequest.getPath());
                assertEquals("POST", recordedRequest.getMethod());
                String body = recordedRequest.getBody().readUtf8();
                org.junit.jupiter.api.Assertions.assertTrue(body.contains("2024-02-01"));
        }

        @Test
        void testIssueModifiedSupplyCostChange() throws Exception {
                // Arrange
                String jsonResponse = "{\"issuanceKey\": \"MODIFIED_KEY_2\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                SupplyCostChangeRequest request = SupplyCostChangeRequest.builder()
                                .date("2024-02-01")
                                .items(Collections.singletonList(TaxInvoiceLineItem.builder()
                                                .date("2024-02-01")
                                                .name("Modified TaxInvoiceLineItem")
                                                .supplyCost(5000L)
                                                .build()))
                                .build();

                // Act
                IssuanceKey key = app.taxInvoices().issueAmendSupplyCost("ORIGINAL_KEY", request);

                // Assert
                assertNotNull(key);
                assertEquals("MODIFIED_KEY_2", key.getValue());

                RecordedRequest recordedRequest = mockWebServer.takeRequest();
                assertEquals("/v1/taxInvoices/ORIGINAL_KEY/amend/changeSupplyCost", recordedRequest.getPath());
                assertEquals("POST", recordedRequest.getMethod());
                String body = recordedRequest.getBody().readUtf8();
                org.junit.jupiter.api.Assertions.assertTrue(body.contains("2024-02-01"));
                org.junit.jupiter.api.Assertions.assertTrue(body.contains("Modified TaxInvoiceLineItem"));
        }
}
