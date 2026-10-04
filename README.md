# GovernAI

**GovernAI** is a context-aware AI governance and decision-support platform that combines public regulatory knowledge, organization-specific policies, AI-generated recommendations, human-in-the-loop review, and auditable decision records.

The goal is not to replace human decision-makers. GovernAI helps users understand relevant rules, retrieve supporting evidence, identify risk, and route important decisions to the appropriate human reviewer.

## Problem

General-purpose AI assistants can answer governance-related questions, but they are not designed to consistently:

- interpret organization-specific policies,
- consider jurisdiction and regulatory context,
- distinguish internal rules from external obligations,
- identify conflicting or restrictive requirements,
- route high-risk decisions to responsible reviewers,
- preserve the original AI recommendation,
- record the final human decision,
- or provide a structured audit trail.

GovernAI addresses this gap by treating governance questions as **decision workflows**, not just chat conversations.

## Core Concept

GovernAI uses several layers of context:

1. Public regulatory knowledge
2. Organization profile and jurisdiction
3. Internal company policies
4. Previous approved decisions
5. Current decision context
6. Human review when required

The core principle is:

> External regulations define the wider constraints.  
> Organization policies define internal rules.  
> AI retrieves and interprets relevant evidence.  
> Workflow determines when human review is required.  
> Humans remain responsible for important decisions.  
> GovernAI records the complete decision process.

## Product Modes

### General Governance Mode

Users without an organization workspace can ask general governance questions.

Example:

> What should a German startup consider before using an external LLM with customer data?

GovernAI uses curated public regulatory and governance sources together with the selected jurisdiction and context.

### Organization Governance Mode

Organizations create their own workspace and provide:

- organization profile,
- country and operating regions,
- industry,
- internal policies,
- security rules,
- architecture guidelines,
- governance documents,
- previous decisions.

GovernAI combines this organization-specific context with relevant external regulatory knowledge.

## MVP Reference Use Case

An employee asks:

> Can I send customer support conversations to OpenAI for summarization?

GovernAI follows this process:

```text
Employee submits question
        ↓
Organization context loaded
        ↓
Internal policies retrieved
        ↓
Relevant regulatory evidence retrieved
        ↓
AI recommendation generated
        ↓
Evidence displayed
        ↓
Risk classified
        ↓
Human review required if necessary
        ↓
Reviewer approves / edits / rejects / escalates
        ↓
Final decision recorded
        ↓
Audit trail stored
```

The AI may initially recommend:

> Do not send identifiable customer support conversations to an unapproved external LLM provider.

If the request is classified as high risk, GovernAI routes it to the configured reviewer.

GovernAI preserves both the original AI recommendation and the final human-approved decision.

## Human-in-the-Loop

Depending on risk and organizational rules, a reviewer can:

- **Approve**
- **Edit**
- **Reject**
- **Escalate**

Example routing:

```text
LOW risk
→ AI recommendation can be returned directly

MEDIUM risk
→ Review may be required

HIGH risk
→ Human approval required
```

Reviewer roles may include:

- Data Protection Officer
- Information Security Officer
- Software Architect
- Compliance Officer
- other organization-specific reviewers.

## Evidence Model

GovernAI distinguishes between different evidence sources.

```text
Internal Evidence
├── AI Usage Policy
├── Data Protection Policy
├── Information Security Policy
└── Architecture Guidelines

External Evidence
├── Regulations
├── Regulatory guidance
└── Other curated governance sources
```

Retrieved evidence can be classified as:

- Supporting
- Restrictive
- Conflicting
- Exception
- Contextual

This enables GovernAI to surface conflicting rules rather than silently selecting one.

## MVP Features

### General Governance

- Ask governance-related questions
- Select jurisdiction
- Retrieve relevant regulatory material
- Generate evidence-backed answers
- Display sources

### Organization Onboarding

- Create organization
- Store country and operating regions
- Store industry and organization context
- Configure reviewer roles

### Policy Management

- Upload policy documents
- Store document metadata
- Extract text
- Split documents into sections
- Prepare documents for retrieval

### AI Decision Support

- Retrieve relevant internal evidence
- Retrieve relevant regulatory evidence
- Generate recommendations
- Explain recommendations
- Display evidence
- Assign risk level

### Human Review

- Approve
- Edit
- Reject
- Escalate

### Audit Trail

Store:

- original question,
- requesting user,
- organization,
- retrieved evidence,
- AI recommendation,
- risk level,
- reviewer,
- reviewer action,
- final decision,
- timestamps.

### Decision History

Users can review previous governance requests and final decisions.

## Technology Stack

### Frontend

- Next.js
- React
- TypeScript

### Core Backend

- Java
- Spring Boot
- Spring Security

### AI Service

- Python
- FastAPI

### Database

- PostgreSQL
- pgvector

### AI / Retrieval

- LLM API
- Embedding model
- Retrieval-Augmented Generation

### Infrastructure

- Docker
- Docker Compose
- GitHub

## High-Level Architecture

```text
                         ┌─────────────────────┐
                         │     Next.js UI      │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Spring Boot Backend │
                         │                     │
                         │ - Organizations     │
                         │ - Users & Roles     │
                         │ - Decisions         │
                         │ - Workflow          │
                         │ - Approvals         │
                         │ - Audit             │
                         └───────┬───────┬─────┘
                                 │       │
                                 │       ▼
                                 │  ┌─────────────────┐
                                 │  │ PostgreSQL      │
                                 │  │ + pgvector      │
                                 │  └─────────────────┘
                                 │
                                 ▼
                         ┌─────────────────────┐
                         │ FastAPI AI Service  │
                         │                     │
                         │ - Parsing           │
                         │ - Retrieval         │
                         │ - RAG               │
                         │ - Recommendation    │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      LLM API        │
                         └─────────────────────┘
```

## Repository Structure

```text
governai/
├── frontend/
├── backend/
├── ai-service/
├── database/
│   ├── migrations/
│   └── seeds/
├── docs/
│   ├── architecture/
│   ├── use-cases/
│   └── erd/
├── synthetic-policies/
├── regulatory-data/
├── docker/
├── scripts/
├── .env.example
├── .gitignore
└── README.md
```

## Database

The initial PostgreSQL schema contains:

- Organization
- User
- Role
- PolicyDocument
- PolicySection
- RegulatoryDocument
- RegulatorySection
- DecisionRequest
- RetrievedEvidence
- AIRecommendation
- Approval
- AuditEvent

`DecisionRequest` acts as the center of the governance workflow.

```text
Organization
│
├── Users
├── Policy Documents
│      └── Policy Sections
│
└── Decision Requests
       ├── Retrieved Evidence
       ├── AI Recommendations
       ├── Approvals
       └── Audit Events

Regulatory Documents
└── Regulatory Sections
```

## Decision Lifecycle

```text
DRAFT
  ↓
ANALYZING
  ↓
Risk Assessment
  ↓
├── LOW → APPROVED / CLOSED
│
└── MEDIUM / HIGH
       ↓
  PENDING_REVIEW
       ↓
  ├── APPROVED
  ├── CONDITIONALLY_APPROVED
  ├── REJECTED
  └── ESCALATED
```

## Risk Levels

The MVP uses:

- `LOW`
- `MEDIUM`
- `HIGH`

Potential high-risk factors include:

- personal data,
- customer data,
- confidential information,
- external AI services,
- security-sensitive systems,
- regulated processing.

## Synthetic Test Organization

The MVP initially uses a fictional organization such as:

**NovaTech GmbH**

```text
Country: Germany
Region: European Union
Industry: Software / SaaS
Data sensitivity: High
```

Initial synthetic policies:

- AI Usage Policy
- Data Protection Policy
- Information Security Policy
- Cloud Services Policy
- Software Architecture Policy

These documents are fictional and created solely for research, development, and testing.

## Getting Started

### Requirements

Install:

- Java 21+
- Maven
- Python 3.11+
- Node.js 20+
- PostgreSQL
- Docker
- Git

### Environment Configuration

```bash
cp .env.example .env
```

Configure the required environment variables.

### Database

Create a PostgreSQL database named:

```text
governai
```

Run:

```text
database/migrations/V1__initial_schema.sql
```

The planned local services are:

```text
Frontend:
http://localhost:3000

Spring Boot Backend:
http://localhost:8080

FastAPI AI Service:
http://localhost:8000
```

## Development Roadmap

### Phase 1: Foundation

- [x] Define MVP
- [x] Define end-to-end use case
- [x] Create ERD
- [x] Create PostgreSQL schema
- [x] Create repository structure
- [ ] Initialize Spring Boot backend
- [ ] Connect PostgreSQL
- [ ] Implement organization entity
- [ ] Implement users and roles

### Phase 2: Policy Management

- [ ] Upload policy documents
- [ ] Store policy metadata
- [ ] Extract document text
- [ ] Create sections and chunks
- [ ] Add embeddings
- [ ] Add semantic retrieval

### Phase 3: AI Decision Support

- [ ] Initialize FastAPI AI service
- [ ] Implement RAG pipeline
- [ ] Add regulatory retrieval
- [ ] Generate evidence-backed recommendations
- [ ] Add citations
- [ ] Add risk classification

### Phase 4: Human-in-the-Loop

- [ ] Create decision workflow
- [ ] Add reviewer routing
- [ ] Implement Approve
- [ ] Implement Edit
- [ ] Implement Reject
- [ ] Implement Escalate

### Phase 5: Audit and History

- [ ] Record audit events
- [ ] Store original AI recommendations
- [ ] Store final human decisions
- [ ] Implement decision history

### Phase 6: Frontend

- [ ] Organization onboarding
- [ ] Ask GovernAI page
- [ ] Decision details page
- [ ] Review page
- [ ] Policy management page
- [ ] Audit history page

### Phase 7: Evaluation

- [ ] Create synthetic policy dataset
- [ ] Create test questions
- [ ] Measure retrieval quality
- [ ] Measure recommendation quality
- [ ] Track human overrides
- [ ] Evaluate decision latency and usability

## MVP Scope

The first version will not attempt to provide:

- every law worldwide,
- automatic legal advice,
- compliance guarantees,
- autonomous high-risk decision-making,
- complex enterprise approval hierarchies,
- advanced autonomous agents,
- production-scale multi-tenancy,
- enterprise billing,
- real-time worldwide regulatory monitoring.

The MVP initially uses a **small curated regulatory dataset focused on Germany and the European Union**.

## Research Direction

GovernAI may later serve as a research platform for questions such as:

> How can human-in-the-loop mechanisms improve the reliability, accountability, and traceability of AI-assisted governance decisions?

Potential evaluation metrics include:

- retrieval relevance,
- recommendation accuracy,
- policy compliance,
- human override rate,
- reasons for human correction,
- hallucinations detected,
- decision time,
- reviewer trust,
- evidence quality.

## Disclaimer

GovernAI is a research and decision-support project.

It is not intended to provide legal advice or replace qualified legal, compliance, security, privacy, or governance professionals.

Regulatory and policy recommendations should be treated as decision-support information and reviewed by responsible humans where appropriate.

## Current Status

**Status: Early MVP development**

Completed:

- MVP definition
- reference use case
- ERD
- PostgreSQL core schema
- repository structure

### Next Milestone

**Initialize the Spring Boot backend and connect it to PostgreSQL.**
