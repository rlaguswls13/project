package com.example.retrospective.repository;

import com.example.retrospective.domain.Activity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to);

    boolean existsByUrl(String url);
}