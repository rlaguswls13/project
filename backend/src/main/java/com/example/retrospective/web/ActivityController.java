package com.example.retrospective.web;

import com.example.retrospective.domain.Activity;
import com.example.retrospective.service.RetrospectiveService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final RetrospectiveService retrospectiveService;

    public ActivityController(RetrospectiveService retrospectiveService) {
        this.retrospectiveService = retrospectiveService;
    }

    @GetMapping
    public List<Activity> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        Instant resolvedFrom = from != null ? from : LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant();
        Instant resolvedTo = to != null ? to : Instant.now();
        return retrospectiveService.findActivities(resolvedFrom, resolvedTo);
    }
}