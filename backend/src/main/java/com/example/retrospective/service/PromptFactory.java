package com.example.retrospective.service;

import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.List;
import java.util.StringJoiner;

final class PromptFactory {

    private PromptFactory() {
    }

    static String buildPrompt(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("You are generating a retrospective summary.");
        joiner.add("Return JSON with whatDid, blockers, nextActions, confidence.");
        joiner.add("Period: " + period.name());
        joiner.add("Date key: " + dateKey);
        joiner.add("Activities:");

        if (activities.isEmpty()) {
            joiner.add("- No activities were collected.");
        } else {
            for (Activity activity : activities) {
                joiner.add("- [" + activity.getSource() + "] " + activity.getTitle() + " (" + activity.getAuthor() + ")");
            }
        }

        return joiner.toString();
    }
}