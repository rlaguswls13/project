package com.example.retrospective.service;

import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CompositeSummaryClient implements SummaryClient {

    private final CopilotSummaryClient copilotSummaryClient;
    private final RuleBasedSummaryClient ruleBasedSummaryClient;

    public CompositeSummaryClient(CopilotSummaryClient copilotSummaryClient, RuleBasedSummaryClient ruleBasedSummaryClient) {
        this.copilotSummaryClient = copilotSummaryClient;
        this.ruleBasedSummaryClient = ruleBasedSummaryClient;
    }

    @Override
    public SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        try {
            return copilotSummaryClient.summarize(activities, period, dateKey);
        } catch (RuntimeException ex) {
            return ruleBasedSummaryClient.summarize(activities, period, dateKey);
        }
    }
}