package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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

        JsonNode response = parseResponse(content);

        return new SummaryResult(
                coerceText(response.get("whatDid")),
                coerceText(response.get("blockers")),
                coerceText(response.get("nextActions")),
                coerceConfidence(response.get("confidence")),
                "COPILOT_SDK",
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

    private JsonNode parseResponse(String content) {
        String json = extractJsonObject(content);
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node == null || !node.isObject()) {
                throw new IllegalStateException("Copilot summary service returned a non-object payload");
            }
            return node;
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Copilot summary service returned invalid JSON", ex);
        }
    }

    /**
     * Copilot responses often wrap the JSON in markdown fences and add prose before or after it.
     * Extract the first balanced {...} object so the payload can be parsed reliably.
     */
    private static String extractJsonObject(String content) {
        int start = content.indexOf('{');
        if (start < 0) {
            throw new IllegalStateException("Copilot summary service returned no JSON object");
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < content.length(); i++) {
            char c = content.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
            } else if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return content.substring(start, i + 1);
                }
            }
        }
        throw new IllegalStateException("Copilot summary service returned an unbalanced JSON object");
    }

    /**
     * Coerce a value that may be a string, number, array, or nested object into a single readable string.
     */
    private static String coerceText(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return "";
        }
        if (node.isValueNode()) {
            return node.asText();
        }
        if (node.isArray()) {
            List<String> parts = new ArrayList<>();
            for (JsonNode element : node) {
                String text = coerceText(element);
                if (!text.isBlank()) {
                    parts.add(text);
                }
            }
            return String.join("; ", parts);
        }
        // Object: prefer common descriptive keys, otherwise join all leaf values.
        for (String key : new String[] {"summary", "description", "issue", "title", "text", "value"}) {
            if (node.hasNonNull(key)) {
                return node.get(key).asText();
            }
        }
        List<String> parts = new ArrayList<>();
        node.fields().forEachRemaining(entry -> {
            String text = coerceText(entry.getValue());
            if (!text.isBlank()) {
                parts.add(text);
            }
        });
        return String.join("; ", parts);
    }

    private static double coerceConfidence(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return 0.5;
        }
        if (node.isNumber()) {
            return node.asDouble();
        }
        if (node.isTextual()) {
            try {
                return Double.parseDouble(node.asText().trim());
            } catch (NumberFormatException ex) {
                return 0.5;
            }
        }
        if (node.isObject()) {
            for (String key : new String[] {"overall", "value", "score"}) {
                if (node.hasNonNull(key) && node.get(key).isNumber()) {
                    return node.get(key).asDouble();
                }
            }
        }
        return 0.5;
    }

    private record CopilotSdkRequest(String prompt, String model) {
    }

    private record CopilotSdkResponse(String content) {
    }
}