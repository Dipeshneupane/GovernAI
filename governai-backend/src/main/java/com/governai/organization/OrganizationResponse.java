package com.governai.organization;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String countryCode,
        String industry,
        String organizationSize,
        RiskLevel dataSensitivity,
        JsonNode operatingRegions,
        JsonNode regulatoryContext,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OrganizationResponse fromEntity(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCountryCode(),
                organization.getIndustry(),
                organization.getOrganizationSize(),
                organization.getDataSensitivity(),
                organization.getOperatingRegions(),
                organization.getRegulatoryContext(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}