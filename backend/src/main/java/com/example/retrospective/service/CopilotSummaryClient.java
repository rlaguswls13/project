package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CopilotSummaryClient implements SummaryClient {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public CopilotSummaryClient(AppProperties appProperties, ObjectMapper objectMapper, RestClient restClient) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    @Override
    public SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        if (!appProperties.getCopilot().isEnabled()
                || appProperties.getCopilot().getSdkUrl().isBlank()) {
            throw new IllegalStateException("Copilot summary service is disabled");
        }

        String content = invokeCopilotSdk(PromptFactory.buildPrompt(activities, period, dateKey));

        CopilotJsonResponse response = parseResponse(content);
        if (response == null) {
            throw new IllegalStateException("Copilot summary service returned no payload");
        }

        return new SummaryResult(
                stringValue(response.whatDid(), ""),
                stringValue(response.blockers(), ""),
                stringValue(response.nextActions(), ""),
                response.confidence() == null ? 0.5 : response.confidence(),
                "COPILOT_SDK_SERVICE",
                appProperties.getCopilot().getModel(),
                "GitHub Copilot SDK sidecar service"
        );
    }

    private String invokeCopilotSdk(String prompt) {
        CopilotSdkResponse response = restClient.post()
                .uri(appProperties.getCopilot().getSdkUrl() + "/api/summarize")
                .body(new CopilotSdkRequest(prompt, appProperties.getCopilot().getModel()))
                .retrieve()
                .body(CopilotSdkResponse.class);

        if (response == null || response.content() == null || response.content().isBlank()) {
            throw new IllegalStateException("Copilot SDK sidecar returned no content");
        }
        return response.content();
    }

    private CopilotJsonResponse parseResponse(String content) {
        try {
            return objectMapper.readValue(content, CopilotJsonResponse.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Copilot summary service returned invalid JSON", ex);
        }
    }

    private static String stringValue(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CopilotJsonResponse(String whatDid, String blockers, String nextActions, Double confidence) {
    }

    private record CopilotSdkRequest(String prompt, String model) {
    }

    private record CopilotSdkResponse(String content) {
    }
}