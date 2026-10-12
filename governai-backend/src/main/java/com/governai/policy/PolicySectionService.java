package com.governai.policy;

import com.governai.exception.PolicyDocumentNotFoundException;
import com.governai.exception.PolicySectionNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PolicySectionService {

    private final PolicySectionRepository policySectionRepository;
    private final PolicyDocumentRepository policyDocumentRepository;

    public PolicySectionService(
            PolicySectionRepository policySectionRepository,
            PolicyDocumentRepository policyDocumentRepository
    ) {
        this.policySectionRepository = policySectionRepository;
        this.policyDocumentRepository = policyDocumentRepository;
    }

    @Transactional
    public PolicySectionResponse createPolicySection(
            PolicySectionRequest request
    ) {

        PolicyDocument policyDocument =
                policyDocumentRepository
                        .findById(request.policyDocumentId())
                        .orElseThrow(() ->
                                new PolicyDocumentNotFoundException(
                                        request.policyDocumentId()
                                )
                        );

        PolicySection policySection = new PolicySection();

        policySection.setPolicyDocument(policyDocument);
        policySection.setSectionNumber(request.sectionNumber());
        policySection.setHeading(request.heading());
        policySection.setContent(request.content());
        policySection.setRuleType(request.ruleType());
        policySection.setRiskLevel(request.riskLevel());
        policySection.setApprovalRequired(
                request.approvalRequired()
        );
        policySection.setApprovalRole(
                request.approvalRole()
        );
        policySection.setMetadata(
                request.metadata()
        );

        PolicySection savedPolicySection =
                policySectionRepository.save(policySection);

        return PolicySectionResponse.fromEntity(
                savedPolicySection
        );
    }

    @Transactional(readOnly = true)
    public PolicySectionResponse getPolicySectionById(
            UUID id
    ) {

        PolicySection policySection =
                policySectionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new PolicySectionNotFoundException(id)
                        );

        return PolicySectionResponse.fromEntity(
                policySection
        );
    }

    @Transactional(readOnly = true)
    public List<PolicySectionResponse> getSectionsByDocument(
            UUID policyDocumentId
    ) {

        if (!policyDocumentRepository.existsById(policyDocumentId)) {
            throw new PolicyDocumentNotFoundException(
                    policyDocumentId
            );
        }

        return policySectionRepository
                .findByPolicyDocument_Id(policyDocumentId)
                .stream()
                .map(PolicySectionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public PolicySectionResponse updatePolicySection(
            UUID id,
            PolicySectionRequest request
    ) {
        PolicySection section = policySectionRepository.findById(id)
                .orElseThrow(() -> new PolicySectionNotFoundException(id));
        PolicyDocument document = policyDocumentRepository.findById(request.policyDocumentId())
                .orElseThrow(() -> new PolicyDocumentNotFoundException(request.policyDocumentId()));
        section.setPolicyDocument(document);
        section.setSectionNumber(request.sectionNumber());
        section.setHeading(request.heading());
        section.setContent(request.content());
        section.setRuleType(request.ruleType());
        section.setRiskLevel(request.riskLevel());
        section.setApprovalRequired(request.approvalRequired());
        section.setApprovalRole(request.approvalRole());
        section.setMetadata(request.metadata());
        return PolicySectionResponse.fromEntity(policySectionRepository.save(section));
    }

    @Transactional
    public void deletePolicySection(UUID id) {
        PolicySection section = policySectionRepository.findById(id)
                .orElseThrow(() -> new PolicySectionNotFoundException(id));
        policySectionRepository.delete(section);
    }
}
