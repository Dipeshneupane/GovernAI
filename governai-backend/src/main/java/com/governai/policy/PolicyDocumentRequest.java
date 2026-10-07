package com.governai.policy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.UUID;

public record PolicyDocumentRequest(

        @NotNull(message = "Organization ID is required")
        UUID organizationId,

        @Size(max = 100)
        String documentCode,

        @NotBlank(message = "Policy title is required")
        @Size(max = 255)
        String title,

        @Size(max = 50)
        String version,

        @Size(max = 100)
        String category,

        DocumentStatus status,

        LocalDate effectiveDate,

        @Size(max = 255)
        String owner,

        @Size(max = 255)
        String approvalAuthority,

        ConfidentialityLevel confidentialityLevel,

        @Size(max = 255)
        String originalFilename,

        String storageLocation,

        JsonNode metadata,

        UUID uploadedBy
) {
}