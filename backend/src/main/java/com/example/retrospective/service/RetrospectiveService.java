package com.example.retrospective.service;

import com.example.retrospective.config.AppProperties;
import com.example.retrospective.domain.Activity;
import com.example.retrospective.domain.Retrospective;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.example.retrospective.repository.ActivityRepository;
import com.example.retrospective.repository.RetrospectiveRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RetrospectiveService {

    private final ActivityRepository activityRepository;
    private final RetrospectiveRepository retrospectiveRepository;
    private final SummaryClient summaryClient;
    private final AppProperties appProperties;

    public RetrospectiveService(ActivityRepository activityRepository, RetrospectiveRepository retrospectiveRepository, SummaryClient summaryClient, AppProperties appProperties) {
        this.activityRepository = activityRepository;
        this.retrospectiveRepository = retrospectiveRepository;
        this.summaryClient = summaryClient;
        this.appProperties = appProperties;
    }

    public Retrospective generate(RetrospectivePeriod period) {
        ZoneId zoneId = appProperties.getRetrospective().getZoneId();
        Range range = resolveRange(period, zoneId);
        List<Activity> activities = activityRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(range.from(), range.to());
        String dateKey = range.dateKey();
        SummaryClient.SummaryResult summary = summaryClient.summarize(activities, period, dateKey);

        Retrospective retrospective = new Retrospective();
        retrospective.setPeriod(period);
        retrospective.setDateKey(dateKey);
        retrospective.setSummary(summary.whatDid());
        retrospective.setBlockers(summary.blockers());
        retrospective.setNextActions(summary.nextActions());
        retrospective.setConfidence(summary.confidence());
        retrospective.setGenerationProvider(summary.generationProvider());
        retrospective.setGenerationModel(summary.generationModel());
        retrospective.setGenerationDetail(summary.generationDetail());
        retrospective.setCreatedAt(Instant.now());
        return retrospectiveRepository.save(retrospective);
    }

    public Optional<Retrospective> latest(RetrospectivePeriod period) {
        return retrospectiveRepository.findFirstByPeriodOrderByCreatedAtDesc(period);
    }

    public List<Activity> findActivities(Instant from, Instant to) {
        return activityRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    private Range resolveRange(RetrospectivePeriod period, ZoneId zoneId) {
        LocalDate today = LocalDate.now(zoneId);
        if (period == RetrospectivePeriod.WEEKLY) {
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Instant from = monday.atStartOfDay(zoneId).toInstant();
            return new Range(from, Instant.now(), monday.toString());
        }

        Instant from = today.atStartOfDay(zoneId).toInstant();
        return new Range(from, Instant.now(), today.toString());
    }

    private record Range(Instant from, Instant to, String dateKey) {
    }
}