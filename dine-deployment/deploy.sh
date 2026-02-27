#!/bin/bash

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

DOMAIN=${DOMAIN:-enceladus.online}

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Initialize directories and configuration
init() {
    log_info "Initializing deployment..."

    mkdir -p nginx/conf.d nginx/ssl certbot/conf certbot/www

    # Generate HTTP nginx config
    sed "s/DOMAIN_PLACEHOLDER/$DOMAIN/g" nginx/conf.d/default.conf.template > nginx/conf.d/default.conf

    log_info "Initialization complete"
}

# Build Docker images
build() {
    log_info "Building Docker images..."

    # Build frontend
    log_info "Building frontend..."
    cd ../dine-frontend
    npm install
    npm run build
    cd ../dine-deployment

    # Build Docker images
    docker compose build

    log_info "Build complete"
}

# Start services
start() {
    log_info "Starting services..."
    docker compose up -d
    log_info "Services started"
}

# Stop services
stop() {
    log_info "Stopping services..."
    docker compose down
    log_info "Services stopped"
}

# Restart services
restart() {
    local service=$1
    if [ -n "$service" ]; then
        log_info "Restarting $service..."
        docker compose restart "$service"
    else
        log_info "Restarting all services..."
        docker compose restart
    fi
}

# View logs
logs() {
    local service=$1
    if [ -n "$service" ]; then
        docker compose logs -f "$service"
    else
        docker compose logs -f
    fi
}

# Update all
update() {
    log_info "Pulling latest code..."
    git pull

    log_info "Rebuilding and restarting..."
    build
    docker compose up -d --build

    log_info "Update complete"
}

# Update frontend only
update-frontend() {
    log_info "Updating frontend..."
    cd ../dine-frontend
    git pull
    npm install
    npm run build
    cd ../dine-deployment
    docker compose restart nginx
    log_info "Frontend updated"
}

# Update backend only
update-backend() {
    log_info "Updating backend..."
    docker compose build backend
    docker compose up -d backend
    log_info "Backend updated"
}

# Update agent only
update-agent() {
    log_info "Updating agent..."
    docker compose build agent
    docker compose up -d agent
    log_info "Agent updated"
}

# Initialize SSL certificate
ssl-init() {
    log_info "Requesting SSL certificate for $DOMAIN..."

    docker compose run --rm certbot certonly \
        --webroot \
        --webroot-path=/var/www/certbot \
        -d "$DOMAIN" \
        --email "$EMAIL" \
        --agree-tos \
        --no-eff-email

    log_info "SSL certificate obtained"
}

# Renew SSL certificate
ssl-renew() {
    log_info "Renewing SSL certificate..."
    docker compose run --rm certbot renew
    docker compose restart nginx
    log_info "SSL certificate renewed"
}

# Generate HTTP nginx config
nginx-http() {
    log_info "Generating HTTP nginx config..."
    sed "s/DOMAIN_PLACEHOLDER/$DOMAIN/g" nginx/conf.d/default.conf.template > nginx/conf.d/default.conf
    log_info "HTTP config generated"
}

# Generate HTTPS nginx config
nginx-https() {
    log_info "Generating HTTPS nginx config..."
    sed "s/DOMAIN_PLACEHOLDER/$DOMAIN/g" nginx/conf.d/default.conf.ssl.template > nginx/conf.d/default.conf
    log_info "HTTPS config generated"
}

# Backup database
db-backup() {
    local backup_file="backup_$(date +%Y%m%d_%H%M%S).sql"
    log_info "Backing up database to $backup_file..."
    docker compose exec -T postgres pg_dump -U "$DB_USER" "$DB_NAME" > "$backup_file"
    log_info "Backup complete: $backup_file"
}

# Show status
status() {
    echo "=== Service Status ==="
    docker compose ps
    echo ""
    echo "=== Resource Usage ==="
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"
}

# Show help
help() {
    echo "Dine Deployment Script"
    echo ""
    echo "Usage: ./deploy.sh <command>"
    echo ""
    echo "Commands:"
    echo "  init            Initialize directories and config"
    echo "  build           Build all Docker images"
    echo "  start           Start all services"
    echo "  stop            Stop all services"
    echo "  restart [svc]   Restart services (optionally specific service)"
    echo "  logs [svc]      View logs (optionally specific service)"
    echo "  update          Update all (pull, build, restart)"
    echo "  update-frontend Update frontend only"
    echo "  update-backend  Update backend only"
    echo "  update-agent    Update agent only"
    echo "  ssl-init        Request SSL certificate"
    echo "  ssl-renew       Renew SSL certificate"
    echo "  nginx-http      Generate HTTP nginx config"
    echo "  nginx-https     Generate HTTPS nginx config"
    echo "  db-backup       Backup database"
    echo "  status          Show service status"
    echo "  help            Show this help"
}

# Main
case "$1" in
    init) init ;;
    build) build ;;
    start) start ;;
    stop) stop ;;
    restart) restart "$2" ;;
    logs) logs "$2" ;;
    update) update ;;
    update-frontend) update-frontend ;;
    update-backend) update-backend ;;
    update-agent) update-agent ;;
    ssl-init) ssl-init ;;
    ssl-renew) ssl-renew ;;
    nginx-http) nginx-http ;;
    nginx-https) nginx-https ;;
    db-backup) db-backup ;;
    status) status ;;
    help|--help|-h) help ;;
    *)
        log_error "Unknown command: $1"
        help
        exit 1
        ;;
esac
