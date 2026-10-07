package com.governai.policy;

import com.governai.exception.OrganizationNotFoundException;
import com.governai.organization.Organization;
import com.governai.organization.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PolicyDocumentService {

    private final PolicyDocumentRepository policyDocumentRepository;
    private final OrganizationRepository organizationRepository;

    public PolicyDocumentService(
            PolicyDocumentRepository policyDocumentRepository,
            OrganizationRepository organizationRepository
    ) {
        this.policyDocumentRepository = policyDocumentRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public PolicyDocumentResponse createPolicyDocument(
            PolicyDocumentRequest request
    ) {

        Organization organization = organizationRepository
                .findById(request.organizationId())
                .orElseThrow(() ->
                        new OrganizationNotFoundException(
                                request.organizationId()
                        )
                );

        PolicyDocument policyDocument = new PolicyDocument();

        policyDocument.setOrganization(organization);
        policyDocument.setDocumentCode(request.documentCode());
        policyDocument.setTitle(request.title());
        policyDocument.setVersion(request.version());
        policyDocument.setCategory(request.category());

        if (request.status() != null) {
            policyDocument.setStatus(request.status());
        }

        policyDocument.setEffectiveDate(request.effectiveDate());
        policyDocument.setOwner(request.owner());
        policyDocument.setApprovalAuthority(
                request.approvalAuthority()
        );
        policyDocument.setConfidentialityLevel(
                request.confidentialityLevel()
        );
        policyDocument.setOriginalFilename(
                request.originalFilename()
        );
        policyDocument.setStorageLocation(
                request.storageLocation()
        );
        policyDocument.setMetadata(request.metadata());
        policyDocument.setUploadedBy(request.uploadedBy());

        PolicyDocument saved =
                policyDocumentRepository.save(policyDocument);

        return PolicyDocumentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PolicyDocumentResponse getPolicyDocumentById(UUID id) {

        PolicyDocument policyDocument =
                policyDocumentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Policy document not found with id: " + id
                                )
                        );

        return PolicyDocumentResponse.fromEntity(policyDocument);
    }

    @Transactional(readOnly = true)
    public List<PolicyDocumentResponse> getByOrganization(
            UUID organizationId
    ) {

        if (!organizationRepository.existsById(organizationId)) {
            throw new OrganizationNotFoundException(organizationId);
        }

        return policyDocumentRepository
                .findByOrganizationId(organizationId)
                .stream()
                .map(PolicyDocumentResponse::fromEntity)
                .toList();
    }
}