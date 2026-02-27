# Dine Deployment

Docker deployment configuration for the Dine AI Restaurant Phone Ordering Platform.

## Services

| Service | Port | Description |
|---------|------|-------------|
| dine-db | 5432 (internal) | PostgreSQL database |
| dine-backend | 8989 (internal) | Spring Boot API |
| dine-agent | 8000 (internal) | Python AI Agent |
| dine-nginx | 80, 443 | Nginx reverse proxy |
| dine-certbot | - | SSL certificate management |

## Quick Start

```bash
# 1. Configure environment
cp .env.example .env
vim .env

# 2. Initialize
./deploy.sh init

# 3. Build and start
./deploy.sh build
./deploy.sh start

# 4. (Optional) Setup SSL
./deploy.sh ssl-init
./deploy.sh nginx-https
./deploy.sh restart nginx
```

## Commands

```bash
./deploy.sh init            # Initialize directories
./deploy.sh build           # Build Docker images
./deploy.sh start           # Start services
./deploy.sh stop            # Stop services
./deploy.sh restart [svc]   # Restart services
./deploy.sh logs [svc]      # View logs
./deploy.sh update          # Full update
./deploy.sh status          # Show status
./deploy.sh ssl-init        # Request SSL cert
./deploy.sh db-backup       # Backup database
```

## URL Routing

```
https://enceladus.online/dine/         → Frontend (SPA)
https://enceladus.online/dine/api/*    → Backend API
https://enceladus.online/dine/agent/*  → AI Agent API
```
