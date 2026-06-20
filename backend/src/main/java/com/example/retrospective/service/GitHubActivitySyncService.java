package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.ActivitySource;
import com.example.retrospective.repository.ActivityRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitHubActivitySyncService {

    private final RestClient restClient;
    private final AppProperties appProperties;
    private final ActivityRepository activityRepository;

    public GitHubActivitySyncService(RestClient restClient, AppProperties appProperties, ActivityRepository activityRepository) {
        this.restClient = restClient;
        this.appProperties = appProperties;
        this.activityRepository = activityRepository;
    }

    public int sync() {
        if (appProperties.getGithub().getOwner().isBlank() || appProperties.getGithub().getRepo().isBlank()) {
            return 0;
        }

        List<Activity> collected = new ArrayList<>();
        collected.addAll(fetchCommits());
        collected.addAll(fetchIssues());
        collected.addAll(fetchPullRequests());

        int saved = 0;
        for (Activity activity : collected) {
            if (!activityRepository.existsByUrl(activity.getUrl())) {
                activityRepository.save(activity);
                saved++;
            }
        }

        return saved;
    }

    private List<Activity> fetchCommits() {
        List<Map<String, Object>> payload = githubGet("/repos/{owner}/{repo}/commits?per_page=25");
        List<Activity> activities = new ArrayList<>();
        for (Map<String, Object> item : payload) {
            Map<String, Object> commit = mapValue(item.get("commit"));
            Map<String, Object> author = mapValue(commit.get("author"));
            String url = stringValue(item.get("html_url"));
            activities.add(toActivity(
                    ActivitySource.COMMIT,
                    stringValue(commit.get("message")),
                    url,
                    stringValue(author.getOrDefault("name", item.get("sha"))),
                    commitDate(author.get("date")),
                    item
            ));
        }
        return activities;
    }

    private List<Activity> fetchIssues() {
        List<Map<String, Object>> payload = githubGet("/repos/{owner}/{repo}/issues?state=all&per_page=25");
        List<Activity> activities = new ArrayList<>();
        for (Map<String, Object> item : payload) {
            if (item.containsKey("pull_request")) {
                continue;
            }
            Map<String, Object> user = mapValue(item.get("user"));
            activities.add(toActivity(
                    ActivitySource.ISSUE,
                    stringValue(item.get("title")),
                    stringValue(item.get("html_url")),
                    stringValue(user.getOrDefault("login", "unknown")),
                    commitDate(item.get("created_at")),
                    item
            ));
        }
        return activities;
    }

    private List<Activity> fetchPullRequests() {
        List<Map<String, Object>> payload = githubGet("/repos/{owner}/{repo}/pulls?state=all&per_page=25");
        List<Activity> activities = new ArrayList<>();
        for (Map<String, Object> item : payload) {
            Map<String, Object> user = mapValue(item.get("user"));
            activities.add(toActivity(
                    ActivitySource.PR,
                    stringValue(item.get("title")),
                    stringValue(item.get("html_url")),
                    stringValue(user.getOrDefault("login", "unknown")),
                    commitDate(item.get("created_at")),
                    item
            ));
        }
        return activities;
    }

    private List<Map<String, Object>> githubGet(String uri) {
        String url = appProperties.getGithub().getApiBaseUrl() + uri;
        return restClient.get()
                .uri(url, appProperties.getGithub().getOwner(), appProperties.getGithub().getRepo())
                .headers(headers -> {
                    if (!appProperties.getGithub().getToken().isBlank()) {
                        headers.setBearerAuth(appProperties.getGithub().getToken());
                    }
                    headers.set(HttpHeaders.USER_AGENT, "retrospective-backend");
                    headers.set("Accept", "application/vnd.github+json");
                    headers.set("X-GitHub-Api-Version", "2022-11-28");
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private Activity toActivity(ActivitySource source, String title, String url, String author, Instant createdAt, Map<String, Object> raw) {
        Activity activity = new Activity();
        activity.setSource(source);
        activity.setTitle(title);
        activity.setUrl(url);
        activity.setAuthor(author);
        activity.setRepoName(appProperties.getGithub().getRepo());
        activity.setCreatedAt(createdAt);
        activity.setRawJson(raw.toString());
        return activity;
    }

    private Map<String, Object> mapValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cast = (Map<String, Object>) map;
            return cast;
        }
        return Map.of();
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }

    private Instant commitDate(Object value) {
        if (value == null) {
            return Instant.now();
        }
        return OffsetDateTime.parse(value.toString()).toInstant();
    }
}