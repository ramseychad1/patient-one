# PatientONE

Multi-tenant case management and patient journey platform for Patient Access Hub (PAH) organizations supporting specialty pharmaceutical manufacturers.

## What it does

Hub case managers use PatientONE to track specialty pharma patients from initial enrollment through ongoing therapy adherence. The system supports multiple pharmaceutical manufacturers, each with multiple drug programs and independently configurable rules.

**Primary user (MVP):** Hub case managers

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.3, Java 21, Spring Security + JWT |
| Frontend | Angular 18, TypeScript, SCSS |
| Database | PostgreSQL 17 (Supabase) |
| ORM | Spring Data JPA + Hibernate 6 |
| Migrations | Flyway (local dev) / Supabase MCP (hosted) |
| Auth | Argon2id password hashing, JWT (15min access / 7day refresh) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| SMS | Mocked for MVP (Twilio interface ready) |

## Project Structure

```
patient-one/
  hub-platform/
    hub-api/                 # Spring Boot REST API
    hub-frontend/            # Angular 18 SPA
    docker-compose.yml       # Local dev (PostgreSQL + API + Frontend)
    .env                     # Connection config (gitignored)
    .env.example             # Template
  prd-artifacts/             # PRD docs and UI mockups (gitignored)
  CLAUDE.md                  # Full build specification
```

## Prerequisites

- Java 21
- Node.js 20+ / npm
- Angular CLI 18 (`npm install -g @angular/cli@18`)
- Docker (optional, for local PostgreSQL)

## Quick Start

### 1. Backend

```bash
cd hub-platform/hub-api
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Starts on http://localhost:8080

### 2. Frontend

```bash
cd hub-platform/hub-frontend
npm install --legacy-peer-deps
ng serve
```

Starts on http://localhost:4200

### 3. Login

| Field | Value |
|-------|-------|
| Email | `admin@hub.com` |
| Password | `admin123` |

### API Documentation

http://localhost:8080/swagger-ui.html

## Database

### Supabase (hosted)

Connection configured in `hub-platform/hub-api/src/main/resources/application-dev.yml`. Uses session pooler for IPv4 compatibility.

### Local (Docker)

```bash
cd hub-platform
docker compose up -d db
```

Then set `SPRING_PROFILES_ACTIVE=default` to use local PostgreSQL on port 5432.

## Data Model

19 tables across 4 layers:

- **Tenant:** Manufacturer, Program, ProgramConfig
- **Patient:** Patient, Prescriber, HubCase, CaseStatusHistory
- **Clinical:** InsurancePlan, BenefitsVerification, EnrollmentRecord, PriorAuthorization, FinancialAssistCase
- **Operations:** CaseTask, Interaction, PatientOutreach
- **Auth:** HubUser, Role, UserRole, UserProgramAssignment

## Key Features

- **Multi-tenancy:** Manufacturer > Program > Case hierarchy with program-level user isolation (RLS)
- **Patient journey:** 8-stage workflow (Referral > Enrollment > BI/BV > PA > Financial Assist > Initiation > Adherence > Closed)
- **Prior Authorization:** SLA tracking with auto-escalation on breach
- **Financial Assistance:** Copay / PAP / Bridge eligibility engine with FPL calculation
- **Compliance:** Copay assistance hard-blocked for government-insured patients (federal law)
- **Outreach:** SMS via pluggable SmsService interface (MockSmsService for dev)
- **Scheduled jobs:** PA SLA monitor (15min), consent expiry (daily), MI reminders (daily), outreach retry (4hr)
- **Dark mode:** Full light/dark theme support across all screens

## API Endpoints

```
POST   /api/v1/auth/login|refresh|logout

GET|POST        /api/v1/manufacturers
GET|PUT         /api/v1/manufacturers/{id}

GET|POST        /api/v1/programs
GET|PUT         /api/v1/programs/{id}
GET|PUT         /api/v1/programs/{id}/config

GET|POST        /api/v1/cases
GET|PATCH       /api/v1/cases/{id}
GET             /api/v1/cases/{id}/timeline

GET|POST        /api/v1/cases/{id}/tasks
PATCH           /api/v1/cases/{id}/tasks/{taskId}
GET             /api/v1/tasks/mine

GET|POST        /api/v1/cases/{id}/outreach
GET|POST        /api/v1/cases/{id}/pa
PATCH           /api/v1/cases/{id}/pa/{paId}

GET             /api/v1/cases/{id}/financial-assistance
POST            /api/v1/cases/{id}/financial-assistance/evaluate

GET|POST        /api/v1/users
GET|PUT         /api/v1/users/{id}
POST            /api/v1/users/{id}/programs
```

## Roles

| Role | Access |
|------|--------|
| HubAdmin | Full system access, bypasses RLS |
| Supervisor | All case access for assigned programs |
| CaseManager | Case CRUD for assigned programs |
| ManufacturerViewer | Read-only for own manufacturer's programs |
