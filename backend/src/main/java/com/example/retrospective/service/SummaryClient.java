package com.example.retrospective.service;

import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.List;

public interface SummaryClient {

    SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey);

    record SummaryResult(
            String whatDid,
            String blockers,
            String nextActions,
            double confidence,
            String generationProvider,
            String generationModel,
            String generationDetail) {
    }
}