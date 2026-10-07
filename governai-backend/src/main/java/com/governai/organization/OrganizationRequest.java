package com.governai.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record OrganizationRequest(

        @NotBlank(message = "Organization name is required")
        @Size(max = 255, message = "Organization name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Country code is required")
        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Country code must be a two-letter uppercase code"
        )
        String countryCode,

        @Size(max = 150, message = "Industry must not exceed 150 characters")
        String industry,

        @Size(max = 50, message = "Organization size must not exceed 50 characters")
        String organizationSize,

        @NotNull(message = "Data sensitivity is required")
        RiskLevel dataSensitivity,

        JsonNode operatingRegions,

        JsonNode regulatoryContext
) {
}