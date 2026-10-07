package com.governai.policy;

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
                                new RuntimeException(
                                        "Policy document not found with id: "
                                                + request.policyDocumentId()
                                )
                        );

        PolicySection section = new PolicySection();

        section.setPolicyDocument(policyDocument);
        section.setSectionNumber(request.sectionNumber());
        section.setHeading(request.heading());
        section.setContent(request.content());
        section.setRuleType(request.ruleType());
        section.setRiskLevel(request.riskLevel());
        section.setApprovalRequired(request.approvalRequired());
        section.setApprovalRole(request.approvalRole());
        section.setMetadata(request.metadata());

        PolicySection saved =
                policySectionRepository.save(section);

        return PolicySectionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PolicySectionResponse getPolicySectionById(UUID id) {

        PolicySection section =
                policySectionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Policy section not found with id: " + id
                                )
                        );

        return PolicySectionResponse.fromEntity(section);
    }

    @Transactional(readOnly = true)
    public List<PolicySectionResponse> getSectionsByDocument(
            UUID policyDocumentId
    ) {

        if (!policyDocumentRepository.existsById(policyDocumentId)) {
            throw new RuntimeException(
                    "Policy document not found with id: "
                            + policyDocumentId
            );
        }

        return policySectionRepository
                .findByPolicyDocument_Id(policyDocumentId)
                .stream()
                .map(PolicySectionResponse::fromEntity)
                .toList();
    }
}