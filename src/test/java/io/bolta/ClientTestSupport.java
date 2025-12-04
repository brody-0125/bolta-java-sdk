package io.bolta;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class ClientTestSupport {
        protected MockWebServer mockWebServer;
        protected BoltaApp app;

        @BeforeEach
        void setUp() throws IOException {
                mockWebServer = new MockWebServer();
                mockWebServer.start();

                // Inject mock client to redirect to mock server
                OkHttpClient mockHttpClient = new OkHttpClient.Builder()
                                .addInterceptor(chain -> {
                                        Request original = chain.request();
                                        // Redirect to mock server
                                        Request request = original.newBuilder()
                                                        .url(mockWebServer.url(original.url().encodedPath()).toString())
                                                        .build();
                                        return chain.proceed(request);
                                })
                                .build();

                BoltaConfig config = BoltaConfig.builder()
                                .apiKey("test_api_key")
                                .build();

                BoltaClient client = BoltaClient.builder()
                                .apiKey(config.getApiKey())
                                .baseUrl(config.getBaseUrl())
                                .httpClient(new io.bolta.http.impl.DefaultHttpClient(mockHttpClient))
                                .build();

                app = BoltaApp.builder()
                                .client(client)
                                .build();
        }

        @AfterEach
        void tearDown() throws IOException {
                mockWebServer.shutdown();
        }
}
