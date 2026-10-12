package com.governai.recommendation;

import com.governai.decision.*;
import com.governai.organization.RiskLevel;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MockRecommendationGeneratorTests {
    private final MockRecommendationGenerator generator = new MockRecommendationGenerator();

    @Test
    void returnsOnlyProvidedEvidenceIdsAsCitations() {
        UUID evidenceId = UUID.randomUUID();
        EvidenceResponse evidence = new EvidenceResponse(evidenceId, UUID.randomUUID(), EvidenceSource.INTERNAL_POLICY,
                UUID.randomUUID(), null, UUID.randomUUID(), "AI Use Policy", "2.1", "Customer data",
                "AI Use Policy", "2.1", "Do not send customer data to public AI services.", null,
                EvidenceType.RESTRICTIVE, OffsetDateTime.now());
        DecisionRequest request = new DecisionRequest();
        request.setQuestion("Can customer data be sent to a public AI service?");
        request.setRiskLevel(RiskLevel.HIGH);

        var result = generator.generate(request, List.of(evidence));

        assertThat(result.citationIds()).containsExactly(evidenceId);
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void requiresCautionWhenNoEvidenceWasRetrieved() {
        DecisionRequest request = new DecisionRequest();
        request.setQuestion("Can we proceed?");

        var result = generator.generate(request, List.of());

        assertThat(result.citationIds()).isEmpty();
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.HIGH);
    }
}
