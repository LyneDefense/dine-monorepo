# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI-powered restaurant phone ordering platform. Customers call, AI agent handles voice interactions (ordering, reservations), backend manages business logic, frontend provides admin panel.

```
Twilio Phone ←→ Python Agent (STT/TTS/LLM) ←→ Java Backend (Spring Boot) ←→ Vue Frontend
```

## Monorepo Structure

- **dine-backend**: Java Spring Boot 3.2.5 API server (PostgreSQL, MyBatis Plus, JWT auth)
- **dine-agent**: Python AI agent for voice interactions (Twilio integration) - *placeholder*
- **dine-frontend**: Vue admin panel - *placeholder*
- **dine-docs**: Product documentation (prd.md contains full system design)
- **dine-deployment**: Deployment configs - *empty*

## Build & Run Commands

### dine-backend

```bash
cd dine-backend

# Build
./mvnw clean package -DskipTests

# Run (requires PostgreSQL on localhost:5432, database: dine_db)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run single test
./mvnw test -Dtest=ClassName#methodName
```

Server runs on port **8989**. API docs available at `/swagger-ui.html`.

### Database

PostgreSQL required. Flyway handles migrations automatically on startup.

```bash
# Create database
createdb dine_db
```

## Backend Architecture

Standard layered architecture: Controller → Service → Mapper → Entity

**Key business modules:**
- Restaurant & Settings (operating hours, parking, tax)
- Menu (categories, items, variants, modifiers, combos)
- Dining (sections, tables)
- Orders (dine-in, takeout with status workflow)
- AI Phone Settings (FAQ, instructions, active hours, escalation rules)
- Account (SUPER_ADMIN, ADMIN roles)

**Notable patterns:**
- Snowflake ID generation (no auto-increment)
- No foreign key constraints (application-level validation via `DataValidator`)
- `MenuItemAlias` entity exists specifically for AI voice recognition
- `selected_modifiers` stored as JSONB in order items

## Git Commit Convention

Do not add co-author or "generated with" footer to commits.
