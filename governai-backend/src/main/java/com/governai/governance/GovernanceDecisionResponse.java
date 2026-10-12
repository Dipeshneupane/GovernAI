package com.governai.governance;
import com.governai.decision.*;
import java.util.List;
public record GovernanceDecisionResponse(DecisionRequestResponse decision, List<EvidenceResponse> evidence, RecommendationResponse recommendation) { }
