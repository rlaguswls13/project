package com.example.retrospective.repository;

import com.example.retrospective.domain.Retrospective;
import com.example.retrospective.domain.RetrospectivePeriod;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

    Optional<Retrospective> findFirstByPeriodOrderByCreatedAtDesc(RetrospectivePeriod period);
}