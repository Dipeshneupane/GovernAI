package com.governai.organization;

import com.governai.exception.OrganizationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationResponse createOrganization(
            OrganizationRequest request
    ) {

        Organization organization = new Organization();

        organization.setName(request.name());
        organization.setCountryCode(request.countryCode());
        organization.setIndustry(request.industry());
        organization.setOrganizationSize(request.organizationSize());
        organization.setDataSensitivity(request.dataSensitivity());
        organization.setOperatingRegions(request.operatingRegions());
        organization.setRegulatoryContext(request.regulatoryContext());

        Organization saved =
                organizationRepository.save(organization);

        return OrganizationResponse.fromEntity(saved);
    }

    public OrganizationResponse getOrganizationById(UUID id) {

        Organization organization =
                organizationRepository.findById(id)
                        .orElseThrow(() -> new OrganizationNotFoundException(id));

        return OrganizationResponse.fromEntity(organization);
    }

    public List<OrganizationResponse> getAllOrganizations() {

        return organizationRepository.findAll()
                .stream()
                .map(OrganizationResponse::fromEntity)
                .toList();
    }
}