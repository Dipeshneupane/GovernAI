package com.governai.policy;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policy-sections")
public class PolicySectionController {

    private final PolicySectionService policySectionService;

    public PolicySectionController(
            PolicySectionService policySectionService
    ) {
        this.policySectionService = policySectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicySectionResponse createPolicySection(
            @Valid @RequestBody PolicySectionRequest request
    ) {
        return policySectionService.createPolicySection(request);
    }

    @GetMapping("/{id}")
    public PolicySectionResponse getPolicySection(
            @PathVariable UUID id
    ) {
        return policySectionService.getPolicySectionById(id);
    }

    @GetMapping("/document/{policyDocumentId}")
    public List<PolicySectionResponse> getSectionsByDocument(
            @PathVariable UUID policyDocumentId
    ) {
        return policySectionService
                .getSectionsByDocument(policyDocumentId);
    }
}