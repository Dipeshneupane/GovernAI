package com.governai.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Writes append-only audit records for governance workflow events. */
@Service
public class AuditService {
    private final AuditEventRepository auditEvents;

    public AuditService(AuditEventRepository auditEvents) {
        this.auditEvents = auditEvents;
    }

    @Transactional
    public void record(UUID organizationId, UUID decisionRequestId, UUID actorId,
                       String eventType, String entityType, UUID entityId) {
        AuditEvent event = new AuditEvent();
        event.setOrganizationId(organizationId);
        event.setDecisionRequestId(decisionRequestId);
        event.setActorId(actorId);
        event.setEventType(eventType);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        auditEvents.save(event);
    }
}
