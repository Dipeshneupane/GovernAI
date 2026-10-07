package com.governai.policy;

import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PolicyDocumentResponse(

        UUID id,
        UUID organizationId,
        String documentCode,
        String title,
        String version,
        String category,
        DocumentStatus status,
        LocalDate effectiveDate,
        String owner,
        String approvalAuthority,
        ConfidentialityLevel confidentialityLevel,
        String originalFilename,
        String storageLocation,
        JsonNode metadata,
        UUID uploadedBy,
        OffsetDateTime uploadedAt

) {

    public static PolicyDocumentResponse fromEntity(
            PolicyDocument policyDocument
    ) {
        return new PolicyDocumentResponse(
                policyDocument.getId(),
                policyDocument.getOrganization().getId(),
                policyDocument.getDocumentCode(),
                policyDocument.getTitle(),
                policyDocument.getVersion(),
                policyDocument.getCategory(),
                policyDocument.getStatus(),
                policyDocument.getEffectiveDate(),
                policyDocument.getOwner(),
                policyDocument.getApprovalAuthority(),
                policyDocument.getConfidentialityLevel(),
                policyDocument.getOriginalFilename(),
                policyDocument.getStorageLocation(),
                policyDocument.getMetadata(),
                policyDocument.getUploadedBy(),
                policyDocument.getUploadedAt()
        );
    }
}