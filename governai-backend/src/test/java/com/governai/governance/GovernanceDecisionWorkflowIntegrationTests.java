package com.governai.governance;

import com.governai.audit.AuditEventRepository;
import com.governai.decision.*;
import com.governai.organization.Organization;
import com.governai.organization.OrganizationRepository;
import com.governai.organization.RiskLevel;
import com.governai.policy.PolicyDocument;
import com.governai.policy.PolicyDocumentRepository;
import com.governai.policy.PolicySection;
import com.governai.policy.PolicySectionRepository;
import com.governai.regulatory.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GovernanceDecisionWorkflowIntegrationTests {
    @Autowired private OrganizationRepository organizations;
    @Autowired private PolicyDocumentRepository policies;
    @Autowired private PolicySectionRepository policySections;
    @Autowired private RegulatoryDocumentRepository regulations;
    @Autowired private RegulatorySectionRepository regulatorySections;
    @Autowired private DecisionRequestRepository decisions;
    @Autowired private AuditEventRepository audits;
    @Autowired private GovernanceDecisionService workflow;

    @Test
    void retrievesPersistedEvidenceGeneratesTraceableRecommendationAndRoutesHighRiskToReview() {
        Organization organization = new Organization();
        organization.setName("Workflow Test GmbH"); organization.setCountryCode("DE");
        organization.setDataSensitivity(RiskLevel.HIGH); organization = organizations.saveAndFlush(organization);

        PolicyDocument policy = new PolicyDocument();
        policy.setOrganization(organization); policy.setTitle("AI Use Policy"); policy = policies.saveAndFlush(policy);
        PolicySection policySection = new PolicySection();
        policySection.setPolicyDocument(policy); policySection.setHeading("Customer data");
        policySection.setContent("Customer data must not be sent to public generative AI services.");
        policySections.saveAndFlush(policySection);

        RegulatoryDocument regulation = new RegulatoryDocument();
        regulation.setTitle("EU Data Protection Guidance"); regulation.setJurisdiction("DE"); regulation = regulations.saveAndFlush(regulation);
        RegulatorySection regulatorySection = new RegulatorySection();
        regulatorySection.setRegulatoryDocument(regulation); regulatorySection.setHeading("Personal data");
        regulatorySection.setContent("Personal customer data requires an appropriate legal basis and safeguards.");
        regulatorySections.saveAndFlush(regulatorySection);

        DecisionRequest decision = new DecisionRequest();
        decision.setOrganizationId(organization.getId()); decision.setJurisdiction("DE");
        decision.setQuestion("Can customer data be sent to a public generative AI service?");
        decision.setRiskLevel(RiskLevel.HIGH); decision = decisions.saveAndFlush(decision);

        GovernanceDecisionResponse result = workflow.analyze(decision.getId());

        assertThat(result.evidence()).hasSize(2);
        assertThat(result.evidence()).allSatisfy(item -> assertThat(item.sourceDocumentId()).isNotNull());
        assertThat(result.recommendation().explanation()).contains("Citation evidence IDs");
        assertThat(result.decision().status()).isEqualTo(DecisionStatus.PENDING_REVIEW);
        assertThat(audits.findByDecisionRequestIdOrderByCreatedAtAsc(decision.getId())).isNotEmpty();
    }
}
