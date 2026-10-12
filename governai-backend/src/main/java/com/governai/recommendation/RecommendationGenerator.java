package com.governai.recommendation;

import com.governai.decision.DecisionRequest;
import com.governai.decision.EvidenceResponse;
import java.math.BigDecimal;
import java.util.List;

/** Boundary for future hosted-LLM integrations. Implementations receive only persisted evidence. */
public interface RecommendationGenerator {
    GeneratedRecommendation generate(DecisionRequest decision, List<EvidenceResponse> evidence);

    record GeneratedRecommendation(String recommendation, String explanation,
                                   com.governai.organization.RiskLevel riskLevel,
                                   BigDecimal confidenceScore, List<java.util.UUID> citationIds) { }
}
