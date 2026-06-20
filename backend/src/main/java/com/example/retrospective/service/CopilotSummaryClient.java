package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CopilotSummaryClient implements SummaryClient {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public CopilotSummaryClient(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        if (!appProperties.getCopilot().isEnabled()
                || appProperties.getCopilot().getBaseUrl().isBlank()
                || appProperties.getCopilot().getApiKey().isBlank()) {
            throw new IllegalStateException("Copilot summary service is disabled");
        }

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(appProperties.getCopilot().getApiKey())
                .baseUrl(appProperties.getCopilot().getBaseUrl())
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(appProperties.getCopilot().getModel())
                .addSystemMessage("Return only valid JSON with fields: whatDid, blockers, nextActions, confidence.")
                .addUserMessage(PromptFactory.buildPrompt(activities, period, dateKey))
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        String content = completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() -> new IllegalStateException("Copilot summary service returned no content"));

        CopilotJsonResponse response = parseResponse(content);
        if (response == null) {
            throw new IllegalStateException("Copilot summary service returned no payload");
        }

        return new SummaryResult(
                stringValue(response.whatDid(), ""),
                stringValue(response.blockers(), ""),
                stringValue(response.nextActions(), ""),
                response.confidence() == null ? 0.5 : response.confidence()
        );
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
}