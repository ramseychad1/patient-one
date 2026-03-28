# PatientONE — Claude Code Project Briefing
## Complete context for autonomous build · Read this first, ask nothing

> **IMPORTANT: The app has been renamed from "HubAccess" to "PatientONE".** All UI references,
> Swagger title, and README use "PatientONE". The Java package is still `com.hubaccess` (not renamed).

---

## Current State (as of 2026-03-28)

The MVP is **fully built and deployed**:

- **Frontend**: https://hub-frontend-production-1fbb.up.railway.app
- **API**: https://hub-api-production-2eed.up.railway.app
- **Swagger**: https://hub-api-production-2eed.up.railway.app/swagger-ui.html
- **Login**: `admin@hub.com` / `admin123`
- **Database**: Supabase project `uenaligvkjwmopjapsyh` (us-east-2), 19 tables deployed
- **Deployment**: Railway (two services: hub-api, hub-frontend)

### Local Development
```bash
# Backend (connects to Supabase via session pooler)
cd hub-platform/hub-api && SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Frontend
cd hub-platform/hub-frontend && ng serve
```

### Key Implementation Notes
- Supabase connection uses session pooler: `aws-1-us-east-2.pooler.supabase.com:5432`
- Flyway is disabled (`spring.flyway.enabled=false`) — schema managed via Supabase MCP
- Hibernate ddl-auto is `none` in dev profile (schema already exists)
- Frontend prod build calls Railway API URL directly (no nginx proxy)
- Admin password was bootstrapped via `/api/v1/auth/bootstrap` endpoint (can be removed)
- User management includes create, edit (inline), delete, and program assignment with multi-select modal
- CORS allows `localhost:4200` and the Railway frontend domain

### Seed Data in Supabase
- 4 roles: HubAdmin, Supervisor, CaseManager, ManufacturerViewer
- Admin user: admin@hub.com (HubAdmin role)
- Manufacturer: Acme Pharma
- Program: Rezdiffra Access Program (with default ProgramConfig)

---

## 0. How to use this document

This document was the original build specification. The app is now built.
Use it as reference for the data model, API contracts, business rules, and UI specs.
The mockup files are in `prd-artifacts/` (gitignored).

When in doubt on an implementation detail not covered here: choose the simpler, more maintainable
option and leave a `// TODO:` comment explaining your choice.

### Non-negotiable pre-condition for all UI work

Before writing any Angular component HTML, CSS, or TypeScript, open the corresponding file
from the `/mockups/` directory and read it. The mockup is the specification. The mapping of
mockup file to component is in section 17. Do not build any screen from memory or inference —
always read the mockup file first. This applies even if you think you remember it.

---

## 1. Project overview

**Product:** HubAccess — a multi-tenant case management and patient journey tool for a
Patient Access Hub (PAH) services organization.

**What it does:** Hub case managers use this system to track specialty pharmaceutical patients
from initial enrollment through ongoing therapy adherence. The hub organization supports multiple
pharmaceutical manufacturers, each with multiple drug programs. Every program has independently
configurable rules.

**Primary user (MVP):** Hub case managers. One role only at MVP. Multi-role support is
designed but not built at MVP.

**Scale expectation:** Hundreds of case managers, tens of manufacturers, hundreds of programs,
tens of thousands of cases. Design for it, but don't over-engineer for it at MVP.

---

## 2. Repository structure

Monorepo. Two top-level service directories.

```
hub-platform/
  hub-api/              # Spring Boot 3.3, Java 21
  hub-frontend/         # Angular 18
  docker-compose.yml    # Local dev only
  .env.example
  README.md
  CLAUDE.md             # This file (copy here at project root)
```

---

## 3. Tech stack — exact versions

### Backend
| Concern | Choice |
|---------|--------|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.3.x |
| Web | Spring Web MVC (not WebFlux) |
| Security | Spring Security 6 + JWT (jjwt 0.12.x) |
| ORM | Spring Data JPA + Hibernate 6 |
| DB migrations | Flyway |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| API docs | SpringDoc OpenAPI 3 (springdoc-openapi-starter-webmvc-ui) |
| Testing | JUnit 5, Mockito, Spring Boot Test, Testcontainers |
| Boilerplate reduction | Lombok |
| DTO mapping | MapStruct |
| Build | Maven |
| Virtual threads | Enabled via `spring.threads.virtual.enabled=true` |

### Frontend
| Concern | Choice |
|---------|--------|
| Framework | Angular 18 |
| Language | TypeScript 5 |
| UI components | Angular Material 18 |
| State | NgRx Signals (or NgRx Store if complexity demands) |
| HTTP | Angular HttpClient with JWT interceptor |
| Forms | Reactive Forms |
| Date library | date-fns |
| Charts | Chart.js via ng2-charts |
| Icons | Angular Material Icons + custom SVG where needed |
| Build | Angular CLI |

### Database
| Concern | Choice |
|---------|--------|
| Engine | PostgreSQL 15 |
| Hosting | Supabase (MCP installed — use it for migrations) |
| Connection pooling | Supabase PgBouncer (built-in) |
| Connection from app | HikariCP (Spring Boot default) |

### Infrastructure
| Concern | Choice |
|---------|--------|
| Containerisation | Docker + Docker Compose (local dev) |
| Deployment | Railway (two services: hub-api, hub-frontend) |
| SMS (outreach) | Mocked for MVP via `MockSmsService` — interface defined, Twilio ready |
| Background jobs | Spring `@Scheduled` (in-process, no external queue at MVP) |
| CI/CD | GitHub Actions (deploy to Railway on merge to main) |

---

## 4. Project structure — Spring Boot

```
hub-api/src/main/java/com/hubaccess/
  HubAccessApplication.java

  config/
    SecurityConfig.java
    CorsConfig.java
    SwaggerConfig.java
    SchedulerConfig.java
    JwtConfig.java

  domain/
    manufacturer/
      Manufacturer.java              # JPA entity
      ManufacturerRepository.java
      ManufacturerService.java
      ManufacturerController.java
      dto/
        ManufacturerDto.java
        CreateManufacturerRequest.java

    program/
      Program.java
      ProgramConfig.java
      ProgramRepository.java
      ProgramConfigRepository.java
      ProgramService.java
      ProgramController.java
      dto/
        ProgramDto.java
        ProgramConfigDto.java
        CreateProgramRequest.java
        UpdateProgramConfigRequest.java

    patient/
      Patient.java
      Prescriber.java
      PatientRepository.java
      PrescriberRepository.java
      PatientService.java
      PatientController.java
      dto/ ...

    cases/
      Case.java                      # Note: 'case' is a Java keyword — use HubCase
      CaseStatusHistory.java
      CaseRepository.java
      CaseService.java
      CaseController.java
      dto/ ...

    insurance/
      InsurancePlan.java
      BenefitsVerification.java
      InsurancePlanRepository.java
      BenefitsVerificationRepository.java
      InsuranceService.java
      InsuranceController.java
      dto/ ...

    enrollment/
      EnrollmentRecord.java
      EnrollmentRepository.java
      EnrollmentService.java
      dto/ ...

    financial/
      FinancialAssistanceCase.java
      PriorAuthorization.java
      FinancialAssistanceRepository.java
      PriorAuthorizationRepository.java
      FinancialAssistanceService.java
      PriorAuthorizationService.java
      FinancialAssistanceController.java
      PriorAuthorizationController.java
      dto/ ...

    outreach/
      PatientOutreach.java
      OutreachRepository.java
      OutreachService.java
      OutreachController.java
      dto/ ...

    activity/
      CaseTask.java
      Interaction.java
      CaseTaskRepository.java
      InteractionRepository.java
      CaseTaskService.java
      InteractionService.java
      CaseTaskController.java
      InteractionController.java
      dto/ ...

    auth/
      HubUser.java
      Role.java
      UserRole.java
      UserProgramAssignment.java
      HubUserRepository.java
      RoleRepository.java
      UserProgramAssignmentRepository.java
      AuthService.java
      UserService.java
      AuthController.java
      UserController.java
      dto/ ...

  security/
    JwtAuthenticationFilter.java
    JwtService.java
    ProgramScopeFilter.java          # Attaches allowed program IDs to SecurityContext
    HubUserDetailsService.java
    HubPermissionEvaluator.java      # @PreAuthorize custom evaluator

  scheduler/
    SlaMonitorJob.java               # Every 15 min — PA deadlines
    ConsentExpiryJob.java            # Daily 06:00
    MiResolutionReminderJob.java     # Daily 08:00
    OutreachRetryJob.java            # Every 4 hours

  outreach/
    SmsService.java                  # Interface
    MockSmsService.java              # @Profile("!production")
    TwilioSmsService.java            # @Profile("production") — stub only at MVP

  financial/
    FplCalculationService.java       # FPL % calculation from income + household size
    FaEligibilityEngine.java         # Evaluates Copay / PAP / Bridge eligibility

  web/
    exception/
      GlobalExceptionHandler.java    # @ControllerAdvice
      ErrorResponse.java
    dto/
      ApiResponse.java               # Standard envelope wrapper

  util/
    CaseNumberGenerator.java         # Generates HUB-YYYY-NNNNN
```

---

## 5. Project structure — Angular

```
hub-frontend/src/app/
  core/
    auth/
      auth.service.ts
      jwt-interceptor.ts
      auth.guard.ts
      program-scope.service.ts       # Holds allowed program IDs from JWT
    models/                          # TypeScript interfaces matching DTOs
    services/
      api.service.ts                 # Base HTTP service

  shared/
    components/
      status-pill/
      journey-progress/
      case-row/
      timeline-entry/
      toggle-switch/
      confirm-dialog/
    pipes/
      relative-time.pipe.ts
      case-status-label.pipe.ts

  features/
    login/
      login.component.ts
      login.component.html           # Matches mockup-login.html

    dashboard/
      dashboard.component.ts         # "My cases" default home
      dashboard.component.html       # Matches mockup-case-manager-branded.html

    cases/
      cases-list/
        cases-list.component.ts
        cases-list.component.html
      case-detail/
        case-detail.component.ts
        case-detail.component.html   # Matches mockup-case-detail.html
        tabs/
          activity-tab/
          pa-tab/
          insurance-tab/
          documents-tab/

    tasks/
      task-queue.component.ts
      task-queue.component.html      # Matches mockup-task-queue.html

    outreach/
      outreach.component.ts

    admin/
      manufacturers/
        manufacturers.component.ts   # Matches mockup-admin.html (tab 1)
        manufacturer-detail.component.ts
      programs/
        programs.component.ts        # Matches mockup-admin.html (tab 2)
        program-config.component.ts  # Matches mockup-program-config.html
      users/
        users.component.ts           # Matches mockup-admin.html (tab 3)
        user-detail.component.ts

  layouts/
    main-layout/
      main-layout.component.ts       # Topbar + sidenav shell
      main-layout.component.html
    auth-layout/
      auth-layout.component.ts       # Login wrapper

  app.routes.ts
  app.config.ts
```

---

## 6. Database entities — complete field specs

### Important naming note
`case` is a reserved word in SQL and Java. Use `hub_case` as the table name.
Use `HubCase` as the Java entity class name.

### All 19 entities

#### MANUFACTURER
```sql
CREATE TABLE manufacturer (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(200) NOT NULL UNIQUE,
  status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active','Inactive')),
  primary_contact_name VARCHAR(200),
  primary_contact_email VARCHAR(200),
  primary_contact_phone VARCHAR(20),
  contract_reference VARCHAR(100),
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID
);
```

#### PROGRAM
```sql
CREATE TABLE program (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  manufacturer_id UUID NOT NULL REFERENCES manufacturer(id),
  name VARCHAR(200) NOT NULL,
  drug_brand_name VARCHAR(200) NOT NULL,
  drug_generic_name VARCHAR(200),
  ndc_codes TEXT,
  therapeutic_area VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active','Inactive','Pilot')),
  program_start_date DATE,
  program_end_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID
);
```

#### PROGRAM_CONFIG
```sql
CREATE TABLE program_config (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  program_id UUID NOT NULL UNIQUE REFERENCES program(id),
  -- Workflow flags
  pa_required BOOLEAN NOT NULL DEFAULT true,
  adherence_program_enabled BOOLEAN NOT NULL DEFAULT true,
  rems_tracking_enabled BOOLEAN NOT NULL DEFAULT false,
  -- Enrollment
  enrollment_sources VARCHAR(20) NOT NULL DEFAULT 'Both' CHECK (enrollment_sources IN ('Portal','eRX','Both')),
  mi_required_for_erx BOOLEAN NOT NULL DEFAULT true,
  mi_required_fields JSONB,
  -- FA flags
  copay_enabled BOOLEAN NOT NULL DEFAULT false,
  pap_enabled BOOLEAN NOT NULL DEFAULT false,
  bridge_enabled BOOLEAN NOT NULL DEFAULT false,
  -- Copay rules
  copay_income_limit_enabled BOOLEAN DEFAULT false,
  copay_income_limit_fpl_pct INTEGER,
  copay_monthly_cap_usd INTEGER,
  copay_annual_cap_usd INTEGER,
  copay_enrollment_months INTEGER DEFAULT 12,
  copay_min_age INTEGER DEFAULT 18,
  -- PAP rules
  pap_fpl_threshold_pct INTEGER,
  pap_allow_commercial_insured BOOLEAN DEFAULT false,
  pap_proof_of_income_required BOOLEAN DEFAULT true,
  pap_attestation_only BOOLEAN DEFAULT false,
  pap_supply_days INTEGER DEFAULT 90,
  pap_enrollment_months INTEGER DEFAULT 12,
  pap_min_age INTEGER DEFAULT 18,
  -- Bridge rules
  bridge_trigger_pa_pending BOOLEAN DEFAULT true,
  bridge_trigger_coverage_lapse BOOLEAN DEFAULT true,
  bridge_trigger_new_enrollment BOOLEAN DEFAULT true,
  bridge_supply_days INTEGER DEFAULT 30,
  bridge_max_episodes_per_year INTEGER DEFAULT 1,
  bridge_new_patient_only BOOLEAN DEFAULT false,
  bridge_income_limit_enabled BOOLEAN DEFAULT false,
  bridge_income_limit_fpl_pct INTEGER,
  -- PA SLA
  pa_sla_submit_days INTEGER DEFAULT 3,
  pa_sla_followup_days INTEGER DEFAULT 5,
  pa_appeal_window_days INTEGER DEFAULT 30,
  pa_auto_escalate_on_breach BOOLEAN DEFAULT true,
  -- Consent
  consent_url_expiry_days INTEGER DEFAULT 7,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### PATIENT
```sql
CREATE TABLE patient (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  date_of_birth DATE NOT NULL,
  gender VARCHAR(20),
  phone_mobile VARCHAR(20),
  phone_home VARCHAR(20),
  email VARCHAR(200),
  address_line1 VARCHAR(200),
  address_line2 VARCHAR(100),
  city VARCHAR(100),
  state CHAR(2),
  zip VARCHAR(10),
  ssn_last4 CHAR(4),              -- Store encrypted
  preferred_language VARCHAR(50) DEFAULT 'en',
  preferred_contact_method VARCHAR(20) DEFAULT 'SMS',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### PRESCRIBER
```sql
CREATE TABLE prescriber (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  npi VARCHAR(10) UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  credential VARCHAR(50),
  practice_name VARCHAR(200),
  phone VARCHAR(20),
  fax VARCHAR(20),
  address_line1 VARCHAR(200),
  city VARCHAR(100),
  state CHAR(2),
  zip VARCHAR(10),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### HUB_CASE (table name: hub_case, Java class: HubCase)
```sql
CREATE TABLE hub_case (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_number VARCHAR(20) UNIQUE NOT NULL,  -- HUB-YYYY-NNNNN, system-generated
  program_id UUID NOT NULL REFERENCES program(id),
  patient_id UUID NOT NULL REFERENCES patient(id),
  prescriber_id UUID REFERENCES prescriber(id),
  assigned_cm_id UUID REFERENCES hub_user(id),
  stage VARCHAR(30) NOT NULL DEFAULT 'Referral'
    CHECK (stage IN ('Referral','Enrollment','BIVB','PA','FinancialAssist','Initiation','Adherence','Closed')),
  status VARCHAR(50) NOT NULL DEFAULT 'Active_Referral',
  enrollment_source VARCHAR(10) CHECK (enrollment_source IN ('Portal','eRX')),
  consent_status VARCHAR(20) NOT NULL DEFAULT 'Pending'
    CHECK (consent_status IN ('Pending','Sent','Received','Expired')),
  consent_received_at TIMESTAMPTZ,
  mi_status VARCHAR(20) CHECK (mi_status IN ('NotRequired','Pending','Sent','Resolved')),
  mi_resolved_at TIMESTAMPTZ,
  priority VARCHAR(10) NOT NULL DEFAULT 'Normal' CHECK (priority IN ('Normal','High','Urgent')),
  sla_breach_flag BOOLEAN NOT NULL DEFAULT false,
  escalation_flag BOOLEAN NOT NULL DEFAULT false,
  closed_reason VARCHAR(200),
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID REFERENCES hub_user(id)
);
```

#### INSURANCE_PLAN
```sql
CREATE TABLE insurance_plan (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  plan_sequence VARCHAR(10) NOT NULL DEFAULT 'Primary' CHECK (plan_sequence IN ('Primary','Secondary')),
  insurance_type_code VARCHAR(30) NOT NULL
    CHECK (insurance_type_code IN ('COMMERCIAL','MARKETPLACE','MEDICARE_A_B','MEDICARE_D',
      'MEDICARE_ADVANTAGE','MEDICAID','TRICARE','VA','UNINSURED','OTHER_GOVERNMENT','UNKNOWN')),
  payer_name VARCHAR(200),
  plan_name VARCHAR(200),
  member_id VARCHAR(100),
  group_id VARCHAR(100),
  rx_bin VARCHAR(20),
  rx_pcn VARCHAR(20),
  effective_date DATE,
  term_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### BENEFITS_VERIFICATION
```sql
CREATE TABLE benefits_verification (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  status VARCHAR(20) NOT NULL DEFAULT 'Pending'
    CHECK (status IN ('Pending','InProgress','Complete','Failed')),
  benefit_type VARCHAR(20) CHECK (benefit_type IN ('Pharmacy','Medical','Both')),
  coverage_confirmed BOOLEAN,
  pa_required_per_bv BOOLEAN,
  patient_copay_amt DECIMAL(10,2),
  deductible_met BOOLEAN,
  oop_max_met BOOLEAN,
  formulary_tier INTEGER,
  coverage_notes TEXT,
  verified_by UUID REFERENCES hub_user(id),
  verified_date DATE,
  reverification_due_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### ENROLLMENT_RECORD
```sql
CREATE TABLE enrollment_record (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL UNIQUE REFERENCES hub_case(id) ON DELETE CASCADE,
  source VARCHAR(10) NOT NULL CHECK (source IN ('Portal','eRX')),
  received_at TIMESTAMPTZ NOT NULL,
  portal_submission_id VARCHAR(100),
  portal_submitted_by VARCHAR(200),
  erx_transaction_id VARCHAR(100),
  erx_prescriber_npi VARCHAR(10),
  erx_drug_name VARCHAR(200),
  erx_quantity DECIMAL(10,2),
  erx_days_supply INTEGER,
  erx_raw_payload JSONB,
  mi_required BOOLEAN DEFAULT false,
  mi_missing_fields JSONB,
  mi_triggered_at TIMESTAMPTZ,
  mi_resolved_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### FINANCIAL_ASSIST_CASE
```sql
CREATE TABLE financial_assist_case (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  fa_type VARCHAR(10) NOT NULL CHECK (fa_type IN ('Copay','PAP','Bridge')),
  status VARCHAR(20) NOT NULL DEFAULT 'Evaluating'
    CHECK (status IN ('Evaluating','Eligible','Ineligible','Approved','Active','Expired','Denied')),
  annual_household_income DECIMAL(12,2),
  household_size INTEGER,
  fpl_percentage_calculated DECIMAL(6,2),
  income_verified BOOLEAN DEFAULT false,
  income_verification_method VARCHAR(20) CHECK (income_verification_method IN ('Document','Attestation')),
  eligibility_determined_at TIMESTAMPTZ,
  eligibility_determined_by UUID REFERENCES hub_user(id),
  approval_date DATE,
  expiry_date DATE,
  benefit_amount_monthly DECIMAL(10,2),
  benefit_ytd_used DECIMAL(10,2) DEFAULT 0,
  bridge_supply_days_authorized INTEGER,
  bridge_ship_date DATE,
  denial_reason TEXT,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### PRIOR_AUTHORIZATION
```sql
CREATE TABLE prior_authorization (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  pa_number VARCHAR(100),
  attempt_number INTEGER NOT NULL DEFAULT 1,
  parent_pa_id UUID REFERENCES prior_authorization(id),
  status VARCHAR(20) NOT NULL DEFAULT 'Draft'
    CHECK (status IN ('Draft','Submitted','UnderReview','Approved','Denied','Appealed','Withdrawn')),
  submitted_date DATE,
  submission_method VARCHAR(20) CHECK (submission_method IN ('Portal','Fax','Phone','Electronic')),
  payer_id VARCHAR(100),
  submitted_by UUID REFERENCES hub_user(id),
  decision_date DATE,
  effective_date DATE,
  expiry_date DATE,
  denial_reason_code VARCHAR(50),
  denial_reason_text TEXT,
  appeal_deadline DATE,
  sla_submit_deadline DATE,
  sla_breached BOOLEAN DEFAULT false,
  clinical_notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### PATIENT_OUTREACH
```sql
CREATE TABLE patient_outreach (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  outreach_type VARCHAR(20) NOT NULL
    CHECK (outreach_type IN ('Consent','MI','RefillReminder','AdherenceCheckin','PAStatus','Other')),
  channel VARCHAR(10) NOT NULL DEFAULT 'SMS' CHECK (channel IN ('SMS')),
  phone_number VARCHAR(20) NOT NULL,
  sent_at TIMESTAMPTZ NOT NULL,
  sent_by UUID REFERENCES hub_user(id),
  unique_url VARCHAR(500),
  access_code VARCHAR(20),
  url_expires_at TIMESTAMPTZ,
  responded BOOLEAN DEFAULT false,
  responded_at TIMESTAMPTZ,
  response_channel VARCHAR(10) CHECK (response_channel IN ('Portal','Chat')),
  attempt_number INTEGER DEFAULT 1,
  message_body TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### CASE_TASK
```sql
CREATE TABLE case_task (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  task_type VARCHAR(30) NOT NULL
    CHECK (task_type IN ('ConsentOutreach','MIOutreach','BIVerification','PASubmission',
      'PAFollowup','FAEvaluation','BridgeArrange','PharmacyOrder','CheckIn','Escalation','Other')),
  title VARCHAR(300) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Open' CHECK (status IN ('Open','InProgress','Completed','Cancelled')),
  priority VARCHAR(10) NOT NULL DEFAULT 'Normal' CHECK (priority IN ('Low','Normal','High','Urgent')),
  due_date DATE,
  assigned_to UUID REFERENCES hub_user(id),
  completed_at TIMESTAMPTZ,
  completed_by UUID REFERENCES hub_user(id),
  notes TEXT,
  auto_generated BOOLEAN DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by UUID REFERENCES hub_user(id)
);
```

#### INTERACTION
```sql
CREATE TABLE interaction (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id) ON DELETE CASCADE,
  interaction_type VARCHAR(30) NOT NULL
    CHECK (interaction_type IN ('InboundCall','OutboundCall','SMS','Email','Note',
      'DocumentReceived','DocumentSent','PrescriptionReceived','PAUpdate','ShipmentUpdate')),
  direction VARCHAR(10) CHECK (direction IN ('Inbound','Outbound','Internal')),
  channel VARCHAR(20) CHECK (channel IN ('Phone','SMS','Email','Portal','System')),
  summary VARCHAR(500) NOT NULL,
  notes TEXT,
  document_reference VARCHAR(500),
  performed_by UUID REFERENCES hub_user(id),
  interaction_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### CASE_STATUS_HISTORY  (append-only — no updates or deletes ever)
```sql
CREATE TABLE case_status_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  case_id UUID NOT NULL REFERENCES hub_case(id),
  from_stage VARCHAR(30),
  to_stage VARCHAR(30) NOT NULL,
  from_status VARCHAR(50),
  to_status VARCHAR(50) NOT NULL,
  changed_by UUID REFERENCES hub_user(id),
  changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  change_reason VARCHAR(500)
);
-- No UPDATE or DELETE should ever be issued against this table.
-- Enforce at DB level with a Postgres rule or trigger if desired.
```

#### HUB_USER
```sql
CREATE TABLE hub_user (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(200) NOT NULL UNIQUE,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active','Inactive','Locked')),
  mfa_enabled BOOLEAN DEFAULT false,
  mfa_secret VARCHAR(200),
  manufacturer_id UUID REFERENCES manufacturer(id),  -- NULL = hub staff
  last_login_at TIMESTAMPTZ,
  failed_login_count INTEGER DEFAULT 0,
  locked_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### ROLE
```sql
CREATE TABLE role (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT,
  permissions JSONB NOT NULL DEFAULT '[]',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- Seed data:
INSERT INTO role (name, description, permissions) VALUES
  ('HubAdmin',          'Full system access',                         '["*"]'),
  ('Supervisor',        'All case access for assigned programs',       '["case:*","task:*","outreach:*","report:read","user:assign"]'),
  ('CaseManager',       'Case access for assigned programs only',      '["case:read","case:write","case:close","task:read","task:write","task:complete","outreach:send","pa:submit","fa:evaluate","report:read"]'),
  ('ManufacturerViewer','Read-only access to own manufacturer programs','["case:read","report:read"]');
```

#### USER_ROLE
```sql
CREATE TABLE user_role (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES hub_user(id) ON DELETE CASCADE,
  role_id UUID NOT NULL REFERENCES role(id),
  granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  granted_by UUID REFERENCES hub_user(id),
  UNIQUE(user_id, role_id)
);
```

#### USER_PROGRAM_ASSIGNMENT  (RLS anchor table)
```sql
CREATE TABLE user_program_assignment (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES hub_user(id) ON DELETE CASCADE,
  program_id UUID NOT NULL REFERENCES program(id) ON DELETE CASCADE,
  access_level VARCHAR(20) NOT NULL DEFAULT 'ReadWrite'
    CHECK (access_level IN ('ReadOnly','ReadWrite','Supervisor')),
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  assigned_by UUID REFERENCES hub_user(id),
  expires_at TIMESTAMPTZ,
  UNIQUE(user_id, program_id)
);
```

---

## 7. Row-level security

### Application layer (primary enforcement)
Every query against `hub_case` and all child tables must filter by program_id:

```java
// In every case-touching repository, add this where clause
// program_id IN (SELECT program_id FROM user_program_assignments WHERE user_id = :userId AND ...)
```

`HubAdmin` users bypass this filter. Detect by checking if user has `*` permission in their role.

### Database layer (defence-in-depth)
Apply Postgres RLS via Supabase MCP after schema creation:

```sql
ALTER TABLE hub_case ENABLE ROW LEVEL SECURITY;
CREATE POLICY case_program_isolation ON hub_case
  FOR ALL USING (
    program_id IN (
      SELECT program_id FROM user_program_assignment
      WHERE user_id = current_setting('app.current_user_id')::uuid
      AND (expires_at IS NULL OR expires_at > NOW())
    )
  );
```

Set `app.current_user_id` at the start of each DB session from the Spring Security context.

---

## 8. Authentication flow

```
POST /api/v1/auth/login
  Body: { email, password }
  Returns: { accessToken (15min JWT), refreshToken (7d) }

JWT payload:
  {
    "sub": "user-uuid",
    "email": "cm@hub.com",
    "roles": ["CaseManager"],
    "programs": ["prog-uuid-1", "prog-uuid-2"],
    "iat": ...,
    "exp": ...
  }

POST /api/v1/auth/refresh
  Body: { refreshToken }
  Returns: { accessToken }

POST /api/v1/auth/logout
  Invalidates refresh token server-side
```

JWT secret stored in environment variable `JWT_SECRET`. Minimum 256-bit key.

---

## 9. REST API conventions

Base path: `/api/v1/`

Standard response envelope:
```json
{
  "data": { ... },
  "meta": {
    "timestamp": "2025-03-28T09:14:00Z",
    "requestId": "uuid"
  }
}
```

Error response:
```json
{
  "error": {
    "code": "CASE_NOT_FOUND",
    "message": "Human-readable message",
    "field": null
  }
}
```

Key endpoints to implement:
```
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout

GET    /api/v1/manufacturers
POST   /api/v1/manufacturers
GET    /api/v1/manufacturers/{id}
PUT    /api/v1/manufacturers/{id}

GET    /api/v1/programs
POST   /api/v1/programs
GET    /api/v1/programs/{id}
PUT    /api/v1/programs/{id}
GET    /api/v1/programs/{id}/config
PUT    /api/v1/programs/{id}/config

GET    /api/v1/cases                    # Filtered by user's program assignments
POST   /api/v1/cases
GET    /api/v1/cases/{id}
PATCH  /api/v1/cases/{id}
GET    /api/v1/cases/{id}/timeline      # All interactions + status history merged

GET    /api/v1/cases/{id}/tasks
POST   /api/v1/cases/{id}/tasks
PATCH  /api/v1/cases/{id}/tasks/{taskId}

GET    /api/v1/cases/{id}/outreach
POST   /api/v1/cases/{id}/outreach      # Triggers SMS send

GET    /api/v1/cases/{id}/pa
POST   /api/v1/cases/{id}/pa
PATCH  /api/v1/cases/{id}/pa/{paId}

GET    /api/v1/cases/{id}/financial-assistance
POST   /api/v1/cases/{id}/financial-assistance/evaluate
PATCH  /api/v1/cases/{id}/financial-assistance/{faId}

GET    /api/v1/users                    # Admin only
POST   /api/v1/users/invite
GET    /api/v1/users/{id}
PUT    /api/v1/users/{id}
POST   /api/v1/users/{id}/programs      # Assign programs to user

GET    /api/v1/tasks/mine               # All open tasks for current CM
```

---

## 10. Scheduled jobs

```java
@Scheduled(fixedDelay = 900_000)       // every 15 min
void checkPaSlaBreaches()              // PA SLA monitor

@Scheduled(cron = "0 0 6 * * *")      // daily 06:00
void checkConsentExpiry()

@Scheduled(cron = "0 0 8 * * *")      // daily 08:00
void checkMiResolutionReminder()

@Scheduled(fixedDelay = 14_400_000)   // every 4 hours
void checkOutreachRetry()
```

---

## 11. SMS service interface

```java
public interface SmsService {
  SmsResult send(String toPhone, String messageBody, String uniqueUrl, String accessCode);
}

// MVP mock — logs to console + returns success
@Service @Profile("!production")
public class MockSmsService implements SmsService { ... }

// Post-MVP
@Service @Profile("production")
public class TwilioSmsService implements SmsService { ... }
```

---

## 12. Financial assistance eligibility engine

```java
// FaEligibilityEngine evaluates all enabled FA sub-programs after BI/BV
// Input: HubCase + InsurancePlan + ProgramConfig
// Output: List<FaEligibilityResult> — one per enabled FA type

// Copay eligibility:
//   - Is copay_enabled?
//   - insurance_type_code IN ('COMMERCIAL', 'MARKETPLACE')
//   - If copay_income_limit_enabled: check income against FPL threshold
//   → Eligible / NotEligible / PendingIncomeVerification

// PAP eligibility:
//   - Is pap_enabled?
//   - insurance_type_code IN ('MEDICARE_A_B','MEDICARE_D','MEDICARE_ADVANTAGE',
//                              'MEDICAID','TRICARE','VA','UNINSURED','OTHER_GOVERNMENT')
//     OR pap_allow_commercial_insured = true
//   - Calculate FPL%: (annual_income / fpl_dollar_for_household_size) * 100
//   - Check <= pap_fpl_threshold_pct
//   → Eligible / NotEligible / PendingIncomeVerification

// Bridge eligibility:
//   - Is bridge_enabled?
//   - Check active qualifying triggers on the case
//   → Eligible / NotEligible / AwaitingTrigger
```

---

## 13. Flyway migration order

```
V001__create_manufacturer.sql
V002__create_program_and_config.sql
V003__create_patient_and_prescriber.sql
V004__create_hub_user_role_assignment.sql   -- hub_user before hub_case (FK dependency)
V005__create_hub_case.sql
V006__create_insurance_enrollment.sql
V007__create_financial_pa_outreach.sql
V008__create_activity_audit.sql
V009__seed_roles.sql
V010__create_indexes.sql
V011__enable_rls_policies.sql
```

---

## 14. Docker setup

### hub-api/Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### hub-frontend/Dockerfile
```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

FROM nginx:alpine
COPY --from=builder /app/dist/hub-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### docker-compose.yml (local dev)
```yaml
version: '3.9'
services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: hub_dev
      POSTGRES_USER: hub
      POSTGRES_PASSWORD: localdevpassword
    ports: ["5432:5432"]
    volumes: [pgdata:/var/lib/postgresql/data]

  api:
    build: ./hub-api
    ports: ["8080:8080"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/hub_dev
      SPRING_DATASOURCE_USERNAME: hub
      SPRING_DATASOURCE_PASSWORD: localdevpassword
      JWT_SECRET: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: dev
    depends_on: [db]

  frontend:
    build: ./hub-frontend
    ports: ["4200:80"]
    depends_on: [api]

volumes:
  pgdata:
```

---

## 15. Environment variables

```bash
# hub-api
SPRING_DATASOURCE_URL=jdbc:postgresql://<supabase-host>:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SUPABASE_DB_PASSWORD=<from-supabase-dashboard>
JWT_SECRET=<min-256-bit-random-string>
SPRING_PROFILES_ACTIVE=production

# hub-frontend (Angular environment.ts)
API_BASE_URL=https://<railway-api-url>/api/v1
```

---

## 16. Brand / design system

**Typeface:** Source Sans 3 (Google Fonts) — open-source Myriad Pro equivalent
```html
<link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@300;400;600;700&family=JetBrains+Mono:wght@400&display=swap" rel="stylesheet">
```

**Color tokens (exact hex — do not change):**
```css
--brand:           #e41f35;   /* Pantone 185 */
--brand-hover:     #c41a2d;
--brand-tint:      #fdf0f1;
--black:           #27251f;   /* Pantone warm black */
--wg7:             #948a85;   /* Pantone Warm Gray 7 */
--wg1:             #efefef;   /* Pantone Warm Gray 1 */
--brand-blue:      #bbdde6;   /* Pantone 7457 — used sparingly */

/* Semantic status colors */
--s-urgent:        #dc2626;   /* Distinct from brand red */
--s-urgent-bg:     #fef2f2;
--s-warn:          #92400e;
--s-warn-bg:       #fffbeb;
--s-info:          #1e40af;
--s-info-bg:       #eff6ff;
--s-ok:            #166534;
--s-ok-bg:         #f0fdf4;
--s-neutral:       #4b5563;
--s-neutral-bg:    #f9fafb;
```

**Theme:** Light and dark mode both supported. Toggle button in topbar. Use CSS custom properties for all colors. Dark mode via `[data-theme="dark"]` on `<html>`.

---

## 17. UI mockups (reference files) — MANDATORY PRE-CONDITION

### Hard rule — do not skip this

**Before writing a single line of HTML, CSS, or TypeScript for any Angular component,
you MUST open and read the corresponding mockup file from the `/mockups/` directory.**

This is not optional. The mockups are the source of truth for every screen. They contain
the exact CSS custom properties, layout structure, color tokens, component hierarchy, and
interaction patterns that the Angular implementation must reproduce. If you write a component
without first reading its mockup, you will deviate and the work will need to be redone.

### Mockup → Component mapping (read the mockup BEFORE building the component)

| Mockup file | Angular component to build | Read before starting |
|-------------|---------------------------|----------------------|
| `/mockups/mockup-login.html` | `features/login/login.component` | ✦ Required |
| `/mockups/mockup-case-manager-branded.html` | `features/dashboard/dashboard.component` | ✦ Required |
| `/mockups/mockup-case-detail.html` | `features/cases/case-detail/case-detail.component` | ✦ Required |
| `/mockups/mockup-task-queue.html` | `features/tasks/task-queue.component` | ✦ Required |
| `/mockups/mockup-program-config.html` | `features/admin/programs/program-config.component` | ✦ Required |
| `/mockups/mockup-admin.html` | `features/admin/manufacturers/`, `programs/`, `users/` | ✦ Required |

### What to extract from each mockup before writing code

1. **CSS custom properties** — copy the `:root` token block verbatim into the Angular
   global styles. Do not invent new color values.
2. **Layout structure** — the HTML hierarchy in the mockup is the Angular template hierarchy.
   Translate div-for-div, not conceptually.
3. **Dark mode** — every mockup supports `[data-theme="dark"]` on `<html>`. Implement this
   via Angular's theme toggle service updating the `data-theme` attribute.
4. **Interactive states** — hover effects, active nav items, filter chip selection, task
   checkbox completion are all demonstrated in the mockups. Match them exactly.
5. **Typography** — Source Sans 3 at the weights shown (300, 400, 600, 700). The mockups
   load it from Google Fonts. Angular app does the same via `index.html`.

### The shared shell (topbar + sidenav)

The main layout shell — topbar with brand stripe, left sidenav with nav items and footer —
appears identically across ALL screens except login. Build it once as `layouts/main-layout`
and extract every visual detail from any one of the non-login mockups. The brand red topbar
stripe, the warm black (`#27251f`) nav background, the active nav item red left-border
indicator, and the user footer are non-negotiable brand elements.

### Non-negotiable visual rules extracted from mockups

- Topbar background: `#27251f` with a 2px `#e41f35` bottom stripe
- Sidenav background: `#0d0c09` (dark) / `#27251f` (same as topbar in practice)
- Active nav item: `rgba(228,31,53,0.15)` background + 3px `#e41f35` left border indicator
- Brand primary button: `#e41f35` fill, `#c41a2d` on hover
- Status pills: use exact hex values from the token table in section 16 — do not approximate
- Case row urgent accent: 3px left border in `#dc2626`
- Page background (light): `#f5f4f0` — warm off-white, not pure white
- Card background (light): `#ffffff`
- All monospace text (case IDs, NDC codes): `JetBrains Mono`

---

## 18. Build order recommendation

1. Flyway migrations + Supabase schema (use Supabase MCP)
2. Spring Boot project scaffold + security config + JWT
3. Auth endpoints (login, refresh, logout)
4. Manufacturer + Program + ProgramConfig CRUD
5. Patient + Prescriber + HubCase CRUD
6. Insurance + Enrollment + BV
7. PA + Financial Assistance + Eligibility Engine
8. Tasks + Interactions + Outreach
9. Scheduled jobs
10. Angular project scaffold + routing + auth guard
11. Login screen
12. Main layout shell (topbar + sidenav)
13. My Cases / case list
14. Case detail
15. Task queue
16. Admin screens (manufacturers, programs, users, program config)
17. Docker setup + Railway deployment config
18. GitHub Actions CI/CD

---

## 19. Key constraints — never violate these

1. `case_status_history` is APPEND-ONLY. Never UPDATE or DELETE rows.
2. `hub_case.case_number` is system-generated (`HUB-YYYY-NNNNN`), never user-entered.
3. Copay assistance is NEVER available to government-insured patients (Medicare, Medicaid,
   TRICARE, VA, Medicare Advantage). This is federal law. Hard-coded exclusion — not configurable.
4. All case queries MUST filter by the requesting user's program assignments (RLS).
5. HubAdmin role (permissions = ["*"]) bypasses RLS. All other roles do not.
6. Hibernate `ddl-auto` must be set to `validate` in all environments. Flyway owns the schema.
7. Never expose JPA entities directly in API responses. Always use DTOs.
8. `ssn_last4` on Patient must be encrypted at the application layer (not just at rest).
9. Passwords hashed with Argon2id via Spring Security's `PasswordEncoder`.
10. JWT secret minimum 256 bits. Never hard-code it — always from environment variable.
