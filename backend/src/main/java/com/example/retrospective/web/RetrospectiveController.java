package com.example.retrospective.web;

import com.example.retrospective.domain.Retrospective;
import com.example.retrospective.domain.RetrospectivePeriod;
import com.example.retrospective.service.GitHubActivitySyncService;
import com.example.retrospective.service.RetrospectiveService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retrospectives")
public class RetrospectiveController {

    private final RetrospectiveService retrospectiveService;
    private final GitHubActivitySyncService gitHubActivitySyncService;

    public RetrospectiveController(RetrospectiveService retrospectiveService, GitHubActivitySyncService gitHubActivitySyncService) {
        this.retrospectiveService = retrospectiveService;
        this.gitHubActivitySyncService = gitHubActivitySyncService;
    }

    @PostMapping("/generate")
    public Retrospective generate(@RequestParam RetrospectivePeriod period) {
        return retrospectiveService.generate(period);
    }

    @GetMapping("/latest")
    public ResponseEntity<Retrospective> latest(@RequestParam RetrospectivePeriod period) {
        Optional<Retrospective> retrospective = retrospectiveService.latest(period);
        return retrospective.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/sync/github")
    public ResponseEntity<String> syncGithub() {
        int synced = gitHubActivitySyncService.sync();
        return ResponseEntity.ok("Synced " + synced + " activities");
    }
}