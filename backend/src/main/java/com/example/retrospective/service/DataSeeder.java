package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.ActivitySource;
import com.example.retrospective.repository.ActivityRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private final ActivityRepository activityRepository;
    private final AppProperties appProperties;

    public DataSeeder(ActivityRepository activityRepository, AppProperties appProperties) {
        this.activityRepository = activityRepository;
        this.appProperties = appProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appProperties.getSeed().isEnabled() || activityRepository.count() > 0) {
            return;
        }

        ZoneId zoneId = appProperties.getRetrospective().getZoneId();
        LocalDate today = LocalDate.now(zoneId);
        Instant morning = today.atTime(9, 0).atZone(zoneId).toInstant();
        Instant midday = today.atTime(13, 0).atZone(zoneId).toInstant();
        Instant afternoon = today.atTime(16, 30).atZone(zoneId).toInstant();

        activityRepository.saveAll(List.of(
                activity(ActivitySource.COMMIT, "Refactor dashboard summary cards", "https://github.com/example/app/commit/1", "dev-a", "example/app", morning),
                activity(ActivitySource.ISSUE, "Fix weekly summary mismatch on KST boundary", "https://github.com/example/app/issues/12", "dev-b", "example/app", midday),
                activity(ActivitySource.PR, "Add activity sync endpoint", "https://github.com/example/app/pull/21", "dev-c", "example/app", afternoon)
        ));
    }

    private Activity activity(ActivitySource source, String title, String url, String author, String repo, Instant createdAt) {
        Activity activity = new Activity();
        activity.setSource(source);
        activity.setTitle(title);
        activity.setUrl(url);
        activity.setAuthor(author);
        activity.setRepoName(repo);
        activity.setCreatedAt(createdAt);
        activity.setRawJson("{\"seed\":true}");
        return activity;
    }
}