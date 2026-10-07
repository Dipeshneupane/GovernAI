package com.governai.policy;

import com.governai.organization.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

public record PolicySectionRequest(

        @NotNull(message = "Policy document ID is required")
        UUID policyDocumentId,

        @Size(max = 50)
        String sectionNumber,

        @Size(max = 255)
        String heading,

        @NotBlank(message = "Section content is required")
        String content,

        RuleType ruleType,

        RiskLevel riskLevel,

        boolean approvalRequired,

        @Size(max = 100)
        String approvalRole,

        JsonNode metadata
) {
}