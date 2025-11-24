package io.bolta;

import io.bolta.model.Customer;
import io.bolta.model.RequestOptions;
import io.bolta.model.RetryOption;
import io.bolta.retry.RangeStatusCodeMatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerTest extends ClientTestSupport {

        @Test
        void testCreateCustomer() throws Exception {
                // Arrange
                String jsonResponse = "{\"id\":\"CUST_123\",\"businessNumber\":\"1234567890\",\"representativeName\":\"John Doe\",\"companyName\":\"My Company\",\"email\":\"test@example.com\",\"address\":\"123 Main St\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(201));

                Customer customer = Customer.builder()
                                .identificationNumber("1234567890")
                                .representativeName("John Doe")
                                .organizationName("My Company")
                                .email1("test@example.com")
                                .address("123 Main St")
                                .build();

                // Act
                app.customers().create(customer);

                // Assert
                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers", request.getPath());
                assertEquals("POST", request.getMethod());
        }

        @Test
        void testCreateCustomerWithRetry() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse().setResponseCode(500)); // Fail once
                mockWebServer.enqueue(new MockResponse().setResponseCode(201)); // Succeed

                Customer customer = Customer.builder()
                                .identificationNumber("1234567890")
                                .representativeName("John Doe")
                                .organizationName("My Company")
                                .email1("test@example.com")
                                .address("123 Main St")
                                .build();

                RequestOptions options = RequestOptions.builder()
                                .retryOption(RetryOption.builder()
                                                .maxAttempts(2)
                                                .retryOnStatusCodes(RangeStatusCodeMatcher.of(500, 599))
                                                .build())
                                .build();

                // Act
                app.customers().create(customer, options);

                // Assert
                assertEquals(2, mockWebServer.getRequestCount());
                mockWebServer.takeRequest(); // First failed request
                RecordedRequest request = mockWebServer.takeRequest(); // Second successful request
                assertEquals("/v1/customers", request.getPath());
        }

        @Test
        void testCreateCustomerAsync() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse().setResponseCode(201));

                Customer customer = Customer.builder()
                                .identificationNumber("1234567890")
                                .representativeName("John Doe")
                                .organizationName("My Company")
                                .email1("test@example.com")
                                .address("123 Main St")
                                .build();

                // Act
                CompletableFuture<Void> future = app.customers().createAsync(customer);
                future.get(); // Wait for completion

                // Assert
                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers", request.getPath());
        }

        @Test
        void testGetCustomer() throws Exception {
                // Arrange
                String jsonResponse = "{\"identificationNumber\":\"1234567890\",\"representativeName\":\"John Doe\",\"organizationName\":\"My Company\",\"email1\":\"test@example.com\",\"address\":\"123 Main St\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                Customer customer = app.customers().get("1234567890");

                // Assert
                assertNotNull(customer);
                assertEquals("1234567890", customer.getIdentificationNumber());

                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers/1234567890", request.getPath());
                assertEquals("GET", request.getMethod());
        }

        @Test
        void testGetCustomerAsync() throws Exception {
                // Arrange
                String jsonResponse = "{\"identificationNumber\":\"1234567890\",\"representativeName\":\"John Doe\",\"organizationName\":\"My Company\",\"email1\":\"test@example.com\",\"address\":\"123 Main St\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                CompletableFuture<Customer> future = app.customers().getAsync("1234567890");
                Customer customer = future.get();

                // Assert
                assertNotNull(customer);
                assertEquals("1234567890", customer.getIdentificationNumber());

                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers/1234567890", request.getPath());
        }

        @Test
        void testGetCertificateRegistrationUrl() throws Exception {
                // Arrange
                String jsonResponse = "{\"url\":\"https://example.com/certificate-registration\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                String url = app.customers().getCertificateRegistrationUrl("customer_bf8paz");

                // Assert
                assertNotNull(url);
                assertEquals("https://example.com/certificate-registration", url);

                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers/customer_bf8paz/certificates/url", request.getPath());
                assertEquals("GET", request.getMethod());
        }

        @Test
        void testGetCertificateRegistrationUrlAsync() throws Exception {
                // Arrange
                String jsonResponse = "{\"url\":\"https://example.com/certificate-registration\"}";
                mockWebServer.enqueue(new MockResponse()
                                .setBody(jsonResponse)
                                .setResponseCode(200));

                // Act
                CompletableFuture<String> future = app.customers()
                                .getCertificateRegistrationUrlAsync("customer_bf8paz");
                String url = future.get();

                // Assert
                assertNotNull(url);
                assertEquals("https://example.com/certificate-registration", url);
        }

        @Test
        void testDeleteCustomer() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(204));

                // Act
                app.customers().deleteCertificate("customer_bf8paz");

                // Assert
                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers/customer_bf8paz/certificates", request.getPath());
                assertEquals("DELETE", request.getMethod());
        }

        @Test
        void testDeleteCustomerAsync() throws Exception {
                // Arrange
                mockWebServer.enqueue(new MockResponse()
                                .setResponseCode(204));

                // Act
                CompletableFuture<Void> future = app.customers().deleteCertificateAsync("customer_bf8paz");
                future.get();

                // Assert
                RecordedRequest request = mockWebServer.takeRequest();
                assertEquals("/v1/customers/customer_bf8paz/certificates", request.getPath());
                assertEquals("DELETE", request.getMethod());
        }
}
