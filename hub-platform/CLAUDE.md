# HubAccess — Patient Access Hub Case Management

Multi-tenant case management and patient journey tool for Patient Access Hub (PAH) organizations.

## Architecture

- **Backend:** Spring Boot 3.3, Java 21, Spring Security + JWT, Spring Data JPA
- **Frontend:** Angular 18, Angular Material, TypeScript
- **Database:** PostgreSQL 15 (Supabase hosted)
- **Deployment:** Docker + Railway

## Project Structure

```
hub-platform/
  hub-api/              # Spring Boot REST API
  hub-frontend/         # Angular 18 SPA
  docker-compose.yml    # Local development
```

## Quick Start

### Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose

### Local Development

```bash
# Start with Docker Compose
docker compose up -d

# Or run services individually:

# Backend
cd hub-api
./mvnw spring-boot:run

# Frontend
cd hub-frontend
npm install --legacy-peer-deps
ng serve
```

### API Documentation
Once running, visit: http://localhost:8080/swagger-ui.html

### Default Test User
- Email: admin@hub.com
- Password: admin123

## Key Features
- Multi-tenant case management (Manufacturer → Program → Case)
- Patient journey tracking (Referral → Adherence)
- Prior Authorization with SLA monitoring
- Financial Assistance eligibility engine (Copay/PAP/Bridge)
- SMS outreach (mocked at MVP)
- Role-based access control with program-level isolation
