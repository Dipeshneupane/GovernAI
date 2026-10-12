package com.governai.recommendation;

import com.governai.decision.*;
import com.governai.organization.RiskLevel;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/** Development-only deterministic generator; it never invents citations. */
@Component
public class MockRecommendationGenerator implements RecommendationGenerator {
    @Override
    public GeneratedRecommendation generate(DecisionRequest decision, List<EvidenceResponse> evidence) {
        List<java.util.UUID> citations = evidence.stream().map(EvidenceResponse::id).toList();
        if (citations.isEmpty()) {
            return new GeneratedRecommendation(
                    "Insufficient verified evidence was retrieved to provide a governance recommendation.",
                    "No active policy or regulatory section matched the supplied organization and jurisdiction context.",
                    RiskLevel.HIGH, BigDecimal.ZERO, citations);
        }
        RiskLevel risk = decision.getRiskLevel() == null ? RiskLevel.MEDIUM : decision.getRiskLevel();
        return new GeneratedRecommendation(
                "Review the retrieved governance evidence before proceeding; apply the restrictive requirements where evidence conflicts.",
                "This deterministic development recommendation is grounded only in the listed retrieved evidence records.",
                risk, BigDecimal.valueOf(0.70), citations);
    }
}
