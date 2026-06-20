package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CopilotSummaryClient implements SummaryClient {

    private final RestClient restClient;
    private final AppProperties appProperties;

    public CopilotSummaryClient(RestClient restClient, AppProperties appProperties) {
        this.restClient = restClient;
        this.appProperties = appProperties;
    }

    @Override
    public SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        if (!appProperties.getCopilot().isEnabled() || appProperties.getCopilot().getBaseUrl().isBlank()) {
            throw new IllegalStateException("Copilot summary service is disabled");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", appProperties.getCopilot().getModel());
        payload.put("period", period.name());
        payload.put("dateKey", dateKey);
        payload.put("responseFormat", "json");
        payload.put("prompt", PromptFactory.buildPrompt(activities, period, dateKey));

        Map<String, Object> response = restClient.post()
                .uri(appProperties.getCopilot().getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + appProperties.getCopilot().getApiKey())
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (response == null) {
            throw new IllegalStateException("Copilot summary service returned no payload");
        }

        return new SummaryResult(
                stringValue(response.get("whatDid"), ""),
                stringValue(response.get("blockers"), ""),
                stringValue(response.get("nextActions"), ""),
                doubleValue(response.get("confidence"), 0.5)
        );
    }

    private static String stringValue(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }

    private static double doubleValue(Object value, double fallback) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value == null) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}