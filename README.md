# portfolio-api-java

REST API for managing a company's project portfolio: lifecycle, team allocation, budget, and risk.

## Requirements

- Java 17
- Maven 3.9+
- Docker (PostgreSQL)

## Setup

### 1. Environment

```bash
cp .env.sample .env
```

Edit `.env` with PostgreSQL and API credentials.

### 2. Database

```bash
docker compose up -d
```

PostgreSQL runs on `localhost:5432` using variables from `.env`.

### 3. Run the API

```bash
./mvnw spring-boot:run
```

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Default auth: `admin` / `admin` (override via `API_USER` / `API_PASSWORD` in `.env`)

## Main endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/external/members` | Mock external API — create member (name + role) |
| `GET` | `/api/external/members/{id}` | Mock external API — get member |
| `POST` | `/api/projects` | Create project |
| `GET` | `/api/projects` | List projects (pagination, optional `?status=`) |
| `GET` | `/api/projects/{id}` | Get project by id |
| `PUT` | `/api/projects/{id}` | Update project |
| `PATCH` | `/api/projects/{id}/status` | Update project status |
| `DELETE` | `/api/projects/{id}` | Delete project (restricted by status) |
| `GET` | `/api/portfolio/report` | Portfolio summary report |

Members are **not** created via the main project API — only through the external mock.

## Example flow

```bash
# 1. Create members (external mock)
curl -u admin:admin -X POST http://localhost:8080/api/external/members \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Manager","role":"MANAGER"}'

curl -u admin:admin -X POST http://localhost:8080/api/external/members \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob Employee","role":"EMPLOYEE"}'

# 2. Create project (use external ids from step 1)
curl -u admin:admin -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Platform",
    "startDate": "2026-01-01",
    "expectedEndDate": "2026-04-01",
    "totalBudget": 80000.00,
    "description": "Internal platform",
    "managerExternalId": 1,
    "memberExternalIds": [2]
  }'
```

## Tests

```bash
./mvnw test          # unit + integration (H2 in-memory)
./mvnw verify        # tests + JaCoCo gate (≥ 70% on services)
```

## Project docs

- [AGENTS.md](./AGENTS.md) — conventions, business rules, architecture
- [PROMPTS.md](./PROMPTS.md) — prompting guide for AI-assisted development
