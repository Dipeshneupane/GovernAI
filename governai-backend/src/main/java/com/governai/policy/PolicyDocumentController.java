package com.governai.policy;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policy-documents")
public class PolicyDocumentController {

    private final PolicyDocumentService policyDocumentService;

    public PolicyDocumentController(
            PolicyDocumentService policyDocumentService
    ) {
        this.policyDocumentService = policyDocumentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyDocumentResponse createPolicyDocument(
            @Valid @RequestBody PolicyDocumentRequest request
    ) {
        return policyDocumentService.createPolicyDocument(request);
    }

    @GetMapping("/{id}")
    public PolicyDocumentResponse getPolicyDocument(
            @PathVariable UUID id
    ) {
        return policyDocumentService.getPolicyDocumentById(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<PolicyDocumentResponse> getOrganizationPolicies(
            @PathVariable UUID organizationId
    ) {
        return policyDocumentService
                .getByOrganization(organizationId);
    }
}