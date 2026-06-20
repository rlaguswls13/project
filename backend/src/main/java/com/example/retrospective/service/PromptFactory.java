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
        joiner.add("You are generating a concise software retrospective summary.");
        joiner.add("Respond with ONLY one minified JSON object and nothing else.");
        joiner.add("Do not use markdown, code fences, comments, or any prose before or after the JSON.");
        joiner.add("The JSON must have exactly these keys, all values plain strings except confidence:");
        joiner.add("  \"whatDid\": string - 1-3 sentences summarizing what was accomplished");
        joiner.add("  \"blockers\": string - blockers or risks, or \"None\" if there are none");
        joiner.add("  \"nextActions\": string - recommended next steps");
        joiner.add("  \"confidence\": number - a single value between 0 and 1");
        joiner.add("Do not nest objects or arrays in any field. Join multiple points into one string.");
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