-- ============================================================
-- GovernAI MVP - PostgreSQL Database Schema
-- Version: 0.1
-- Purpose: Core schema + controlled enum types
-- ============================================================

BEGIN;

-- Required for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE risk_level_enum AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH'
);

CREATE TYPE decision_status_enum AS ENUM (
    'DRAFT',
    'ANALYZING',
    'PENDING_REVIEW',
    'APPROVED',
    'CONDITIONALLY_APPROVED',
    'REJECTED',
    'ESCALATED',
    'CLOSED'
);

CREATE TYPE approval_action_enum AS ENUM (
    'APPROVE',
    'EDIT',
    'REJECT',
    'ESCALATE'
);

CREATE TYPE document_status_enum AS ENUM (
    'DRAFT',
    'ACTIVE',
    'SUPERSEDED',
    'ARCHIVED'
);

CREATE TYPE rule_type_enum AS ENUM (
    'PERMISSIVE',
    'RESTRICTIVE',
    'REQUIRED',
    'SUPPORTIVE',
    'EXCEPTION',
    'INFORMATIONAL'
);

CREATE TYPE evidence_source_enum AS ENUM (
    'INTERNAL_POLICY',
    'REGULATION',
    'REGULATORY_GUIDANCE',
    'PREVIOUS_DECISION',
    'ARCHITECTURE_DECISION'
);

CREATE TYPE evidence_type_enum AS ENUM (
    'SUPPORTING',
    'RESTRICTIVE',
    'CONFLICTING',
    'EXCEPTION',
    'CONTEXTUAL'
);

CREATE TYPE confidentiality_level_enum AS ENUM (
    'PUBLIC',
    'INTERNAL',
    'CONFIDENTIAL',
    'RESTRICTED'
);

-- ============================================================
-- 1. ORGANIZATION
-- ============================================================

CREATE TABLE organization (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(255) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    industry VARCHAR(150),

    organization_size VARCHAR(50),
    data_sensitivity risk_level_enum,

    operating_regions JSONB,
    regulatory_context JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 2. USER
-- ============================================================

CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID REFERENCES organization(id)
        ON DELETE CASCADE,

    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 3. ROLE
-- ============================================================

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- ============================================================
-- 4. USER <-> ROLE
-- ============================================================

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES app_user(id)
        ON DELETE CASCADE,

    role_id UUID NOT NULL REFERENCES role(id)
        ON DELETE CASCADE,

    PRIMARY KEY (user_id, role_id)
);

-- ============================================================
-- 5. POLICY DOCUMENT
-- ============================================================

CREATE TABLE policy_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID NOT NULL REFERENCES organization(id)
        ON DELETE CASCADE,

    document_code VARCHAR(100),

    title VARCHAR(255) NOT NULL,
    version VARCHAR(50),
    category VARCHAR(100),

    status document_status_enum NOT NULL DEFAULT 'ACTIVE',

    effective_date DATE,

    owner VARCHAR(255),
    approval_authority VARCHAR(255),

    confidentiality_level confidentiality_level_enum,

    original_filename VARCHAR(255),
    storage_location TEXT,

    metadata JSONB,

    uploaded_by UUID REFERENCES app_user(id),

    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 6. POLICY SECTION
-- ============================================================

CREATE TABLE policy_section (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    policy_document_id UUID NOT NULL REFERENCES policy_document(id)
        ON DELETE CASCADE,

    section_number VARCHAR(50),
    heading VARCHAR(255),

    content TEXT NOT NULL,

    rule_type rule_type_enum,
    risk_level risk_level_enum,

    approval_required BOOLEAN NOT NULL DEFAULT FALSE,
    approval_role VARCHAR(100),

    metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 7. REGULATORY DOCUMENT
-- ============================================================

CREATE TABLE regulatory_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title VARCHAR(255) NOT NULL,
    document_code VARCHAR(100),

    jurisdiction VARCHAR(100) NOT NULL,
    authority VARCHAR(255),
    category VARCHAR(100),

    version VARCHAR(50),
    status document_status_enum NOT NULL DEFAULT 'ACTIVE',

    effective_date DATE,
    source_url TEXT,

    metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 8. REGULATORY SECTION
-- ============================================================

CREATE TABLE regulatory_section (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    regulatory_document_id UUID NOT NULL
        REFERENCES regulatory_document(id)
        ON DELETE CASCADE,

    section_number VARCHAR(50),
    heading VARCHAR(255),

    content TEXT NOT NULL,

    metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 9. DECISION REQUEST
-- ============================================================

CREATE TABLE decision_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID REFERENCES organization(id)
        ON DELETE CASCADE,

    requested_by UUID REFERENCES app_user(id),

    question TEXT NOT NULL,

    purpose TEXT,
    provider VARCHAR(255),
    data_type VARCHAR(255),

    additional_context JSONB,

    jurisdiction VARCHAR(100),

    risk_level risk_level_enum,

    status decision_status_enum NOT NULL DEFAULT 'DRAFT',

    human_review_required BOOLEAN NOT NULL DEFAULT FALSE,

    assigned_reviewer UUID REFERENCES app_user(id),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 10. RETRIEVED EVIDENCE
-- ============================================================

CREATE TABLE retrieved_evidence (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    decision_request_id UUID NOT NULL
        REFERENCES decision_request(id)
        ON DELETE CASCADE,

    source_type evidence_source_enum NOT NULL,

    policy_section_id UUID
        REFERENCES policy_section(id),

    regulatory_section_id UUID
        REFERENCES regulatory_section(id),

    source_title VARCHAR(255),
    section_reference VARCHAR(100),

    evidence_text TEXT NOT NULL,

    relevance_score NUMERIC(6,5),

    evidence_type evidence_type_enum,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (
        policy_section_id IS NOT NULL
        OR regulatory_section_id IS NOT NULL
    )
);

-- ============================================================
-- 11. AI RECOMMENDATION
-- ============================================================

CREATE TABLE ai_recommendation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    decision_request_id UUID NOT NULL
        REFERENCES decision_request(id)
        ON DELETE CASCADE,

    recommendation TEXT NOT NULL,
    explanation TEXT,

    identified_risk_level risk_level_enum,

    confidence_score NUMERIC(6,5),

    model_provider VARCHAR(100),
    model_name VARCHAR(100),
    model_version VARCHAR(100),

    prompt_version VARCHAR(100),

    metadata JSONB,

    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 12. APPROVAL
-- ============================================================

CREATE TABLE approval (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    decision_request_id UUID NOT NULL
        REFERENCES decision_request(id)
        ON DELETE CASCADE,

    reviewer_id UUID NOT NULL
        REFERENCES app_user(id),

    action approval_action_enum NOT NULL,

    original_recommendation TEXT,
    final_decision TEXT,
    reviewer_comment TEXT,

    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 13. AUDIT EVENT
-- ============================================================

CREATE TABLE audit_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    organization_id UUID REFERENCES organization(id),

    decision_request_id UUID
        REFERENCES decision_request(id)
        ON DELETE CASCADE,

    actor_id UUID REFERENCES app_user(id),

    event_type VARCHAR(100) NOT NULL,

    entity_type VARCHAR(100),
    entity_id UUID,

    previous_state JSONB,
    new_state JSONB,
    event_metadata JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_user_organization
    ON app_user(organization_id);

CREATE INDEX idx_policy_document_organization
    ON policy_document(organization_id);

CREATE INDEX idx_policy_section_document
    ON policy_section(policy_document_id);

CREATE INDEX idx_regulatory_section_document
    ON regulatory_section(regulatory_document_id);

CREATE INDEX idx_decision_organization
    ON decision_request(organization_id);

CREATE INDEX idx_decision_requested_by
    ON decision_request(requested_by);

CREATE INDEX idx_decision_assigned_reviewer
    ON decision_request(assigned_reviewer);

CREATE INDEX idx_decision_status
    ON decision_request(status);

CREATE INDEX idx_decision_risk
    ON decision_request(risk_level);

CREATE INDEX idx_evidence_decision
    ON retrieved_evidence(decision_request_id);

CREATE INDEX idx_ai_recommendation_decision
    ON ai_recommendation(decision_request_id);

CREATE INDEX idx_approval_decision
    ON approval(decision_request_id);

CREATE INDEX idx_audit_decision
    ON audit_event(decision_request_id);

CREATE INDEX idx_audit_organization
    ON audit_event(organization_id);

-- ============================================================
-- INITIAL ROLES
-- ============================================================

INSERT INTO role (name, description) VALUES
    ('ADMIN', 'Manages organization settings, users and policy documents'),
    ('EMPLOYEE', 'Submits governance questions and decision requests'),
    ('REVIEWER', 'Reviews AI recommendations and governance decisions'),
    ('DATA_PROTECTION_OFFICER', 'Reviews privacy and personal-data related decisions'),
    ('SECURITY_OFFICER', 'Reviews information security related decisions'),
    ('ARCHITECT', 'Reviews software and architecture-related decisions')
ON CONFLICT (name) DO NOTHING;

COMMIT;

-- ============================================================
-- End of GovernAI MVP schema
-- ============================================================
