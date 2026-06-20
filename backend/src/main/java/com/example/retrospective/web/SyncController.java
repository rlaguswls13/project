package com.example.retrospective.web;

import com.example.retrospective.service.GitHubActivitySyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final GitHubActivitySyncService gitHubActivitySyncService;

    public SyncController(GitHubActivitySyncService gitHubActivitySyncService) {
        this.gitHubActivitySyncService = gitHubActivitySyncService;
    }

    @PostMapping("/github")
    public ResponseEntity<String> syncGithub() {
        int synced = gitHubActivitySyncService.sync();
        return ResponseEntity.ok("Synced " + synced + " activities");
    }
}