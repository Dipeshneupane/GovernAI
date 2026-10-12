package com.governai.retrieval;

import com.governai.audit.AuditService;
import com.governai.decision.*;
import com.governai.exception.ResourceNotFoundException;
import com.governai.policy.PolicySection;
import com.governai.policy.PolicySectionRepository;
import com.governai.policy.DocumentStatus;
import com.governai.regulatory.RegulatorySection;
import com.governai.regulatory.RegulatorySectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/** Deterministic keyword retrieval; replaceable by vector retrieval later. */
@Service
public class EvidenceRetrievalService {
    private static final int MAX_RESULTS_PER_SOURCE = 5;
    private final DecisionRequestRepository decisions;
    private final PolicySectionRepository policySections;
    private final RegulatorySectionRepository regulatorySections;
    private final RetrievedEvidenceRepository evidence;
    private final AuditService auditService;

    public EvidenceRetrievalService(DecisionRequestRepository decisions, PolicySectionRepository policySections,
                                    RegulatorySectionRepository regulatorySections, RetrievedEvidenceRepository evidence,
                                    AuditService auditService) {
        this.decisions = decisions; this.policySections = policySections; this.regulatorySections = regulatorySections;
        this.evidence = evidence; this.auditService = auditService;
    }

    @Transactional
    public List<EvidenceResponse> retrieve(UUID decisionId) {
        DecisionRequest decision = decisions.findById(decisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Decision request", decisionId));
        List<EvidenceResponse> results = new ArrayList<>();
        if (decision.getOrganizationId() != null) {
            rank(policySections.findByPolicyDocument_Organization_Id(decision.getOrganizationId()).stream()
                            .filter(section -> section.getPolicyDocument().getStatus() == DocumentStatus.ACTIVE).toList(), decision.getQuestion(), this::policyText)
                    .limit(MAX_RESULTS_PER_SOURCE).forEach(section -> results.add(savePolicyEvidence(decision, section)));
        }
        if (decision.getJurisdiction() != null && !decision.getJurisdiction().isBlank()) {
            rank(regulatorySections.findByRegulatoryDocument_JurisdictionIgnoreCase(decision.getJurisdiction()).stream()
                            .filter(section -> section.getRegulatoryDocument().getStatus() == DocumentStatus.ACTIVE).toList(), decision.getQuestion(), this::regulatoryText)
                    .limit(MAX_RESULTS_PER_SOURCE).forEach(section -> results.add(saveRegulatoryEvidence(decision, section)));
        }
        return results;
    }

    private EvidenceResponse savePolicyEvidence(DecisionRequest decision, PolicySection section) {
        RetrievedEvidence item = new RetrievedEvidence(); item.setDecisionRequestId(decision.getId());
        item.setSourceType(EvidenceSource.INTERNAL_POLICY); item.setPolicySectionId(section.getId());
        item.setSourceTitle(section.getPolicyDocument().getTitle()); item.setSectionReference(section.getSectionNumber());
        item.setEvidenceText(section.getContent()); item.setRelevanceScore(score(section.getContent(), decision.getQuestion()));
        item.setEvidenceType(EvidenceType.CONTEXTUAL); item = evidence.save(item);
        auditService.record(decision.getOrganizationId(), decision.getId(), null, "EVIDENCE_RETRIEVED", "RetrievedEvidence", item.getId());
        return EvidenceResponse.withProvenance(item, section.getPolicyDocument().getId(), section.getPolicyDocument().getTitle(), section.getSectionNumber(), section.getHeading());
    }
    private EvidenceResponse saveRegulatoryEvidence(DecisionRequest decision, RegulatorySection section) {
        RetrievedEvidence item = new RetrievedEvidence(); item.setDecisionRequestId(decision.getId());
        item.setSourceType(EvidenceSource.REGULATION); item.setRegulatorySectionId(section.getId());
        item.setSourceTitle(section.getRegulatoryDocument().getTitle()); item.setSectionReference(section.getSectionNumber());
        item.setEvidenceText(section.getContent()); item.setRelevanceScore(score(section.getContent(), decision.getQuestion()));
        item.setEvidenceType(EvidenceType.CONTEXTUAL); item = evidence.save(item);
        auditService.record(decision.getOrganizationId(), decision.getId(), null, "EVIDENCE_RETRIEVED", "RetrievedEvidence", item.getId());
        return EvidenceResponse.withProvenance(item, section.getRegulatoryDocument().getId(), section.getRegulatoryDocument().getTitle(), section.getSectionNumber(), section.getHeading());
    }
    private <T> java.util.stream.Stream<T> rank(List<T> sections, String question, java.util.function.Function<T, String> text) {
        return sections.stream().filter(s -> score(text.apply(s), question).compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing((T s) -> score(text.apply(s), question)).reversed());
    }
    private BigDecimal score(String text, String question) { long matches = terms(question).stream().filter(term -> text.toLowerCase(Locale.ROOT).contains(term)).count(); return BigDecimal.valueOf(Math.min(1d, matches / (double) Math.max(1, terms(question).size()))).setScale(5, java.math.RoundingMode.HALF_UP); }
    private Set<String> terms(String value) { return Arrays.stream(value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")).filter(t -> t.length() > 2).collect(java.util.stream.Collectors.toSet()); }
    private String policyText(PolicySection s) { return String.join(" ", Objects.toString(s.getHeading(), ""), s.getContent()); }
    private String regulatoryText(RegulatorySection s) { return String.join(" ", Objects.toString(s.getHeading(), ""), s.getContent()); }
}
