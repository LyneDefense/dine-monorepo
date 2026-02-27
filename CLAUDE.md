# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI-powered restaurant phone ordering platform. Customers call, AI agent handles voice interactions (ordering, reservations), backend manages business logic, frontend provides admin panel.

```
Twilio Phone ←→ Python Agent (STT/TTS/LLM) ←→ Java Backend (Spring Boot) ←→ Vue Frontend
```

## Monorepo Structure

- **dine-backend**: Java Spring Boot 3.2.5 API server (PostgreSQL, MyBatis Plus, JWT auth)
- **dine-agent**: Python FastAPI AI agent for voice interactions (Twilio, OpenAI)
- **dine-frontend**: Vue 3 + TypeScript admin panel (Vite, Pinia, Vue Router)
- **dine-docs**: Product documentation (prd.md contains full system design)
- **dine-deployment**: Docker deployment configs

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

### dine-frontend

```bash
cd dine-frontend

# Install dependencies
npm install

# Dev server (port 5173)
npm run dev

# Build for production
npm run build

# Type check
npm run build:check
```

Base path: `/dine/`. API proxy: `/dine/api/*` → `localhost:8989/api/*`

### dine-agent

```bash
cd dine-agent

# Create virtual environment
python -m venv venv
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run dev server (port 8000)
uvicorn src.main:app --reload
```

### dine-deployment

```bash
cd dine-deployment

# Initialize
./deploy.sh init

# Build all
./deploy.sh build

# Start/Stop services
./deploy.sh start
./deploy.sh stop

# View logs
./deploy.sh logs [service]

# SSL setup
./deploy.sh ssl-init
./deploy.sh nginx-https
```

### Database

PostgreSQL required. Flyway handles migrations automatically on startup.

```bash
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
- Account (SUPER_ADMIN, ADMIN, STAFF roles)
- Platform Admin (SUPER_ADMIN only: create restaurants with admin accounts)

**Multi-tenant Permission System:**
- SUPER_ADMIN: Platform admin, `restaurantId=NULL`, can access all restaurants
- ADMIN: Restaurant owner/manager, can only access their own restaurant
- STAFF: Employee, read-only access (view menu, process orders)
- `@RestaurantAccess` annotation + AOP aspect for tenant isolation
- `SecurityContextUtils` for permission checking in code

**Notable patterns:**
- Snowflake ID generation (no auto-increment)
- No foreign key constraints (application-level validation via `DataValidator`)
- `MenuItemAlias` entity exists specifically for AI voice recognition
- `selected_modifiers` stored as JSONB in order items

## URL Routing (Production)

```
https://enceladus.online/dine/         → Frontend (SPA)
https://enceladus.online/dine/api/*    → Backend API
https://enceladus.online/dine/agent/*  → AI Agent API
```

## Git Commit Convention

Do not add co-author or "generated with" footer to commits.

## Maintenance Notes

- 如果你觉得项目有变动，在有必要的时候，需要提醒我重新审视和更新项目的CLAUDE.md文件。
