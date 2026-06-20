package com.example.retrospective.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "retrospectives")
public class Retrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RetrospectivePeriod period;

    @Column(nullable = false, length = 30)
    private String dateKey;

    @Lob
    @Column(nullable = false)
    private String summary;

    @Lob
    @Column(nullable = false)
    private String blockers;

    @Lob
    @Column(nullable = false)
    private String nextActions;

    @Column(nullable = false)
    private double confidence;

    @Column(length = 80)
    private String generationProvider;

    @Column(length = 160)
    private String generationModel;

    @Lob
    private String generationDetail;

    @Column(nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RetrospectivePeriod getPeriod() {
        return period;
    }

    public void setPeriod(RetrospectivePeriod period) {
        this.period = period;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBlockers() {
        return blockers;
    }

    public void setBlockers(String blockers) {
        this.blockers = blockers;
    }

    public String getNextActions() {
        return nextActions;
    }

    public void setNextActions(String nextActions) {
        this.nextActions = nextActions;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getGenerationProvider() {
        return generationProvider;
    }

    public void setGenerationProvider(String generationProvider) {
        this.generationProvider = generationProvider;
    }

    public String getGenerationModel() {
        return generationModel;
    }

    public void setGenerationModel(String generationModel) {
        this.generationModel = generationModel;
    }

    public String getGenerationDetail() {
        return generationDetail;
    }

    public void setGenerationDetail(String generationDetail) {
        this.generationDetail = generationDetail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}