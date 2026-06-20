package com.example.retrospective.scheduler;

import com.example.retrospective.domain.RetrospectivePeriod;
import com.example.retrospective.service.RetrospectiveService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetrospectiveScheduler {

    private final RetrospectiveService retrospectiveService;

    public RetrospectiveScheduler(RetrospectiveService retrospectiveService) {
        this.retrospectiveService = retrospectiveService;
    }

    @Scheduled(cron = "0 0 18 * * MON-FRI", zone = "Asia/Seoul")
    public void generateDaily() {
        retrospectiveService.generate(RetrospectivePeriod.DAILY);
    }

    @Scheduled(cron = "0 30 18 * * FRI", zone = "Asia/Seoul")
    public void generateWeekly() {
        retrospectiveService.generate(RetrospectivePeriod.WEEKLY);
    }
}