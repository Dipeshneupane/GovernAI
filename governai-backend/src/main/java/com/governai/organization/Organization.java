package com.governai.organization;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "industry", length = 150)
    private String industry;

    @Column(name = "organization_size", length = 50)
    private String organizationSize;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "data_sensitivity",
            columnDefinition = "risk_level_enum"
    )
    private RiskLevel dataSensitivity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "operating_regions", columnDefinition = "jsonb")
    private JsonNode operatingRegions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "regulatory_context", columnDefinition = "jsonb")
    private JsonNode regulatoryContext;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Organization() {
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getOrganizationSize() {
        return organizationSize;
    }

    public void setOrganizationSize(String organizationSize) {
        this.organizationSize = organizationSize;
    }

    public RiskLevel getDataSensitivity() {
        return dataSensitivity;
    }

    public void setDataSensitivity(RiskLevel dataSensitivity) {
        this.dataSensitivity = dataSensitivity;
    }

    public JsonNode getOperatingRegions() {
        return operatingRegions;
    }

    public void setOperatingRegions(JsonNode operatingRegions) {
        this.operatingRegions = operatingRegions;
    }

    public JsonNode getRegulatoryContext() {
        return regulatoryContext;
    }

    public void setRegulatoryContext(JsonNode regulatoryContext) {
        this.regulatoryContext = regulatoryContext;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}