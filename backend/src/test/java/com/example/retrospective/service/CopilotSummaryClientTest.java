package com.example.retrospective.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class CopilotSummaryClientTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void summarizeThrowsWhenCopilotIsDisabled() {
        AppProperties properties = new AppProperties();
        properties.getCopilot().setEnabled(false);
        properties.getCopilot().setSdkUrl(mockWebServer.url("/").toString());
        properties.getCopilot().setModel("auto");

        CopilotSummaryClient client = new CopilotSummaryClient(properties, new ObjectMapper(), RestClient.builder().build());

        assertThatThrownBy(() -> client.summarize(List.of(), RetrospectivePeriod.DAILY, "2026-06-20"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void summarizeReturnsStructuredResultFromSidecarResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"content\":\"{\\\"whatDid\\\":\\\"Implemented sync\\\",\\\"blockers\\\":\\\"None\\\",\\\"nextActions\\\":\\\"Ship tests\\\",\\\"confidence\\\":0.93}\"}"));

        AppProperties properties = new AppProperties();
        properties.getCopilot().setEnabled(true);
        properties.getCopilot().setSdkUrl(mockWebServer.url("/").toString());
        properties.getCopilot().setModel("gpt-4.1");

        CopilotSummaryClient client = new CopilotSummaryClient(properties, new ObjectMapper(), RestClient.builder().build());
        SummaryClient.SummaryResult result = client.summarize(List.of(), RetrospectivePeriod.DAILY, "2026-06-20");

        assertThat(result.generationProvider()).isEqualTo("COPILOT_SDK");
        assertThat(result.generationModel()).isEqualTo("gpt-4.1");
        assertThat(result.whatDid()).isEqualTo("Implemented sync");
        assertThat(result.blockers()).isEqualTo("None");
        assertThat(result.nextActions()).isEqualTo("Ship tests");
        assertThat(result.confidence()).isEqualTo(0.93);
    }
}
