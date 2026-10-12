package com.governai.governance;

import com.governai.audit.AuditService;
import com.governai.decision.*;
import com.governai.exception.ResourceNotFoundException;
import com.governai.organization.RiskLevel;
import com.governai.recommendation.RecommendationGenerator;
import com.governai.retrieval.EvidenceRetrievalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/** Coordinates the evidence-grounded decision workflow without exposing AI-provider details. */
@Service
public class GovernanceDecisionService {
    private final DecisionRequestRepository decisions;
    private final AiRecommendationRepository recommendations;
    private final EvidenceRetrievalService retrieval;
    private final RecommendationGenerator generator;
    private final AuditService auditService;

    public GovernanceDecisionService(DecisionRequestRepository decisions, AiRecommendationRepository recommendations,
                                     EvidenceRetrievalService retrieval, RecommendationGenerator generator,
                                     AuditService auditService) {
        this.decisions = decisions; this.recommendations = recommendations; this.retrieval = retrieval;
        this.generator = generator; this.auditService = auditService;
    }

    @Transactional
    public GovernanceDecisionResponse analyze(UUID decisionId) {
        DecisionRequest decision = decisions.findById(decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Decision request", decisionId));
        if (decision.getStatus() != DecisionStatus.DRAFT && decision.getStatus() != DecisionStatus.ANALYZING) {
            throw new IllegalArgumentException("Only draft or analyzing decisions can be analyzed");
        }
        decision.setStatus(DecisionStatus.ANALYZING);
        List<EvidenceResponse> evidence = retrieval.retrieve(decisionId);
        var generated = generator.generate(decision, evidence);
        AiRecommendation recommendation = new AiRecommendation();
        recommendation.setDecisionRequestId(decisionId);
        recommendation.setRecommendation(generated.recommendation());
        recommendation.setExplanation(generated.explanation() + " Citation evidence IDs: " + generated.citationIds());
        recommendation.setIdentifiedRiskLevel(generated.riskLevel());
        recommendation.setConfidenceScore(generated.confidenceScore());
        recommendation.setModelProvider("local"); recommendation.setModelName("deterministic-governance-generator");
        recommendation.setModelVersion("1"); recommendation.setPromptVersion("evidence-only-v1");
        recommendation = recommendations.save(recommendation);
        boolean reviewRequired = decision.isHumanReviewRequired() || generated.riskLevel() == RiskLevel.HIGH || evidence.isEmpty();
        decision.setHumanReviewRequired(reviewRequired);
        decision.setStatus(reviewRequired ? DecisionStatus.PENDING_REVIEW : DecisionStatus.APPROVED);
        decisions.save(decision);
        auditService.record(decision.getOrganizationId(), decisionId, null, "RECOMMENDATION_GENERATED", "AiRecommendation", recommendation.getId());
        return new GovernanceDecisionResponse(DecisionRequestResponse.from(decision), evidence, RecommendationResponse.from(recommendation));
    }
}
