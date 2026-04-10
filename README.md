# Buzz — AI-Powered News Aggregation Platform

An end-to-end AI-integrated news pipeline built with Java and Spring Boot. Aggregates content from multiple sources, processes it through a local LLM for summarization and quality scoring, and serves it via a clean REST API with category filtering, trending detection, and full-text search.

---

## Features

- **AI Summarization Pipeline** — Articles are fetched, queued via Redis, processed by a local LLM (Gemma3:4b), and persisted only if they pass quality and similarity checks
- **90% Performance Improvement** — Pipeline execution time reduced from ~10 minutes to under 1 minute through async Redis message queues
- **LLM Benchmarking** — Tested Gemma3 and Qwen3 variants from 0.5B to 30B parameters; selected Gemma3:4b for optimal speed/quality on local hardware
- **Category System** — Slug-based category management with ADMIN-only write access
- **News Feed** — Paginated news listing with category filtering, trending detection, and keyword search
- **Auth** — JWT-based register/login flow with role support (`USER`, `ADMIN`)
- **User Management** — Profile view and update, user lookup by ID

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| Queue | Redis (async pipeline) |
| AI Runtime | Ollama (Gemma3:4b) |
| Auth | JWT, Spring Security |
| Sources | NewsAPI, RSS Feeds, Web Scraping |

---

## Architecture

```
NewsAPI / RSS Feeds / Web Scraping
            │
            ▼
     Async Ingestion Layer
     (Redis Message Queue)
            │
            ▼
     AI Processing (Ollama — Gemma3:4b)
     ├── Summarization
     ├── Quality Scoring
     └── Similarity Filtering
            │
            ▼
       PostgreSQL
            │
            ▼
     REST API (Spring Boot)
     ├── News Feed (paginated, category, trending, search)
     ├── Category Management
     └── Auth & User Management
```

---

## API Overview

### Auth — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/login` | Login and receive JWT |

### News — `/api/v1/news`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | Public | All news (paginated) |
| GET | `/{id}` | Public | Get news by ID |
| GET | `/category/{slug}` | Public | News by category slug |
| GET | `/trending` | Public | Trending news (paginated) |
| GET | `/search?keyword=` | Public | Full-text keyword search |
| POST | `/fetch` | ADMIN | Trigger news fetch → Redis pipeline |
| POST | `/` | ADMIN | Manually create a news article |
| PUT | `/{id}` | ADMIN | Update a news article |
| DELETE | `/{id}` | ADMIN | Delete a news article |

### Categories — `/api/v1/categories`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | Public | All active categories |
| GET | `/{slug}` | Public | Get category by slug |
| POST | `/` | ADMIN | Create category |
| PUT | `/{id}` | ADMIN | Update category |
| DELETE | `/{id}` | ADMIN | Delete category |

### Users — `/api/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/me` | Authenticated | Get own profile |
| PUT | `/me` | Authenticated | Update own profile |
| GET | `/{id}` | Public | Get user by ID |
| DELETE | `/{id}` | Public | Delete user |

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Redis
- [Ollama](https://ollama.com) with Gemma3:4b pulled locally

```bash
ollama pull gemma3:4b
```

### Setup

```bash
git clone https://github.com/your-username/buzz.git
cd buzz

# Configure src/main/resources/application.properties
./mvnw spring-boot:run
```

### Key Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/buzz_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.data.redis.host=localhost
spring.data.redis.port=6379

jwt.secret=your_jwt_secret
jwt.expiration=86400000

ollama.base-url=http://localhost:11434
news-api.key=your_newsapi_key
```

---

## Project Status

Active development — core pipeline, news feed, and auth are complete and stable.
