package com.example.retrospective.service;

import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedSummaryClient implements SummaryClient {

    @Override
    public SummaryResult summarize(List<Activity> activities, RetrospectivePeriod period, String dateKey) {
        if (activities.isEmpty()) {
            return new SummaryResult(
                    "No activity collected for " + period.name().toLowerCase(Locale.ROOT) + " " + dateKey,
                    "No blockers captured.",
                    "Connect the GitHub repo/token and run synchronization.",
                    0.25
            );
        }

        List<String> titles = activities.stream()
                .map(Activity::getTitle)
                .toList();

        Map<String, Long> frequency = titles.stream()
                .flatMap(title -> List.of(title.toLowerCase(Locale.ROOT).split("\\s+")).stream())
                .filter(token -> token.length() > 3)
                .collect(Collectors.groupingBy(token -> token, Collectors.counting()));

        String whatDid = titles.stream()
                .limit(5)
                .collect(Collectors.joining("; "));

        List<String> blockers = detectBlockers(titles);
        String blockerText = blockers.isEmpty() ? "No blocker keywords detected." : String.join("; ", blockers);

        List<String> nextActions = new ArrayList<>();
        nextActions.add("Review the most repeated keyword: " + mostFrequentToken(frequency));
        nextActions.add("Prepare follow-up on the top activity source: " + topSource(activities));

        double confidence = Math.min(0.95, 0.45 + (activities.size() * 0.05));
        return new SummaryResult(whatDid, blockerText, String.join("; ", nextActions), confidence);
    }

    private List<String> detectBlockers(List<String> titles) {
        return titles.stream()
                .filter(title -> containsAny(title, List.of("block", "bug", "fail", "error", "delay", "issue")))
                .map(title -> "Potential blocker: " + title)
                .toList();
    }

    private boolean containsAny(String title, List<String> terms) {
        String lower = title.toLowerCase(Locale.ROOT);
        return terms.stream().anyMatch(lower::contains);
    }

    private String mostFrequentToken(Map<String, Long> frequency) {
        return frequency.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("activity");
    }

    private String topSource(List<Activity> activities) {
        return activities.stream()
                .collect(Collectors.groupingBy(Activity::getSource, Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(entry -> entry.getKey().name())
                .orElse("COMMIT");
    }
}