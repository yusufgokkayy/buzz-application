# Buzz — AI-Powered News Aggregation Platform

An end-to-end AI-integrated news pipeline built with Java and Spring Boot. Aggregates content from multiple sources, summarizes it with a locally hosted LLM, and serves it via a REST API with category filtering, trending news and keyword search.

---

## Features

- **AI Summarization Pipeline** — Articles are fetched from NewsAPI, their full text is scraped with Jsoup, queued on a Redis Stream, summarized by a local LLM (Gemma3:4b) and persisted to PostgreSQL
- **Reliable Queue Consumption** — Redis Streams consumer group with explicit `XACK`: a message is acknowledged only after it is processed, so failures are retried instead of lost (at-least-once delivery)
- **90% Performance Improvement** — Pipeline execution time reduced from ~10 minutes to under 1 minute by moving to an asynchronous Redis Streams producer/consumer design
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
| Sources | NewsAPI + full-article scraping (Jsoup) |

---

## Architecture

```
NewsAPI → full-text scraping (Jsoup)
            │
            ▼
     Async Ingestion Layer
     (Redis Stream + consumer group)
            │
            ▼
     AI Processing (Ollama — Gemma3:4b)
     └── Turkish summarization
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
| GET | `/{id}` | Authenticated | Get user by ID |
| DELETE | `/{id}` | ADMIN | Delete user |

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
git clone https://github.com/yusufgokkayy/buzz-application.git
cd buzz-application

# Configure src/main/resources/application.yml
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

## License

All Rights Reserved. This code is published for viewing and evaluation only; see [LICENSE](LICENSE).
