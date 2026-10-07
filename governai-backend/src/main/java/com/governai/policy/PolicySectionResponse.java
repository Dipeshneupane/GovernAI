package com.governai.policy;

import com.governai.organization.RiskLevel;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PolicySectionResponse(

        UUID id,
        UUID policyDocumentId,
        String sectionNumber,
        String heading,
        String content,
        RuleType ruleType,
        RiskLevel riskLevel,
        boolean approvalRequired,
        String approvalRole,
        JsonNode metadata,
        OffsetDateTime createdAt

) {

    public static PolicySectionResponse fromEntity(
            PolicySection section
    ) {
        return new PolicySectionResponse(
                section.getId(),
                section.getPolicyDocument().getId(),
                section.getSectionNumber(),
                section.getHeading(),
                section.getContent(),
                section.getRuleType(),
                section.getRiskLevel(),
                section.isApprovalRequired(),
                section.getApprovalRole(),
                section.getMetadata(),
                section.getCreatedAt()
        );
    }
}