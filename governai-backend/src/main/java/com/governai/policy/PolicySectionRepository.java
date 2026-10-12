package com.governai.policy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicySectionRepository
        extends JpaRepository<PolicySection, UUID> {

    List<PolicySection> findByPolicyDocument_Id(UUID policyDocumentId);

    List<PolicySection> findByPolicyDocument_Organization_Id(UUID organizationId);
}
