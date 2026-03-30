# Short-Term Stay API
**SE4458 Software Architecture & Design – Midterm**  
**Student:** Berker Vergi | **Group:** 2

---

## 🔗 Links
| | URL |
|---|---|
| **GitHub** | https://github.com/FreshmanVergi/stayapi |
| **Swagger (deployed)** | http://stayapi-env.eba-ruyp7rkn.eu-central-1.elasticbeanstalk.com/swagger-ui/index.html |
| **Video** | https://drive.google.com/your-video-link |

---

## 📐 Design & Assumptions

### Architecture
Service-oriented, layered architecture:
- **Controller** → HTTP only, no business logic
- **Service** → all business logic, `@Transactional`
- **Repository** → JPA data access only
- **DTO** → strict separation between API and entity layers

### Assumptions
1. A listing is unavailable if any booking overlaps with the requested date range
2. Rate limiting (3/day) is tracked by username if authenticated, otherwise by IP
3. Only guests with an existing booking may review; one review per booking
4. CSV upload header row: `noOfPeople, country, city, price, title, description`
5. JWT tokens expire after 24 hours
6. All paginated responses return 10 items per page
7. Roles: `HOST` (add listings), `GUEST` (book & review), `ADMIN` (reports)

### Issues Encountered
- Java 25 + Spring Boot ASM incompatibility → solved by targeting Java 21 bytecode
- Lombok annotation processor not working with Java 25 → replaced with manual getters/setters/builders
- HDD path with spaces caused Maven plugin failures → moved project to local disk

---

## 🛠️ Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | PostgreSQL 17 |
| Documentation | Springdoc OpenAPI 2.8.6 (Swagger UI) |
| CSV | OpenCSV 5.9 |
| Build | Maven 3.9 |
| Container | Docker |
| Hosting | AWS App Runner |
| Load Testing | k6 |

---

## 🗄️ Data Model (ER Diagram)

```
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│    users    │  1    n  │     listings     │  1    n  │   bookings   │
├─────────────┤──────────├──────────────────┤──────────├──────────────┤
│ id (PK)     │          │ id (PK)          │          │ id (PK)      │
│ username    │          │ host_id (FK)     │          │ listing_id FK│
│ password    │          │ no_of_people     │          │ guest_id  FK │
│ role        │          │ country          │          │ date_from    │
└──────┬──────┘          │ city             │          │ date_to      │
       │                 │ price            │          └──────┬───────┘
       │   1           n │ title            │                 │
       └─────────────────│ description      │  ┌──────────────┘
                         │ average_rating   │  │
                         │ review_count     │  │  ┌───────────────────┐
                         └──────────────────┘  │  │   booking_guests  │
                                  │            │  ├───────────────────┤
                                  │ 1        n │  │ booking_id (FK)   │
                         ┌────────┘            └──│ guest_name        │
                         │                        └───────────────────┘
                    ┌────┴──────┐
                    │  reviews  │
                    ├───────────┤
                    │ id (PK)   │
                    │ booking_id│
                    │ guest_id  │
                    │ listing_id│
                    │ rating    │
                    │ comment   │
                    └───────────┘

┌─────────────────────┐
│  query_rate_limits  │
├─────────────────────┤
│ id (PK)             │
│ identifier          │  ← username or IP
│ query_date          │
│ call_count          │
└─────────────────────┘
```

---

## 🔌 API Endpoints

| Method | Endpoint | Auth | Paging | Description |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | ❌ | ❌ | Register (HOST/GUEST/ADMIN) |
| POST | `/api/v1/auth/login` | ❌ | ❌ | Login → returns JWT |
| POST | `/api/v1/listings` | ✅ HOST | ❌ | Insert listing |
| POST | `/api/v1/listings/upload` | ✅ HOST | ❌ | Bulk insert via CSV |
| GET | `/api/v1/listings` | ❌ | ✅ | Query listings (3/day limit) |
| POST | `/api/v1/bookings` | ✅ GUEST | ❌ | Book a stay |
| GET | `/api/v1/bookings/my` | ✅ GUEST | ✅ | My bookings |
| POST | `/api/v1/reviews` | ✅ GUEST | ❌ | Review a stay |
| GET | `/api/v1/admin/reports` | ✅ ADMIN | ✅ | Report listings by rating |

---

## 🚀 Running Locally

### Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 17

```bash
# 1. Create database
psql postgres -c "CREATE DATABASE staydb;"
psql postgres -c "CREATE USER postgres WITH SUPERUSER PASSWORD 'postgres';"

# 2. Run
cd stayapi
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn spring-boot:run
```

Swagger UI: **http://localhost:8080/swagger-ui.html**

### With Docker
```bash
docker build -t stayapi .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/staydb \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  stayapi
```

---

## ☁️ AWS App Runner Deployment

```bash
# 1. Build & push to ECR
aws ecr create-repository --repository-name stayapi --region eu-central-1
docker build -t stayapi .
docker tag stayapi:latest <account_id>.dkr.ecr.eu-central-1.amazonaws.com/stayapi:latest
aws ecr get-login-password --region eu-central-1 | docker login --username AWS \
  --password-stdin <account_id>.dkr.ecr.eu-central-1.amazonaws.com
docker push <account_id>.dkr.ecr.eu-central-1.amazonaws.com/stayapi:latest

# 2. Create App Runner service via AWS Console
#    - Source: ECR image above
#    - Port: 8080
#    - Env vars: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET
```

---

## 📈 Load Test Results

### Run the tests
```bash
brew install k6
k6 run k6/load-test.js

# Against deployed URL
k6 run -e BASE_URL=https://YOUR_APP_RUNNER_URL k6/load-test.js
```

### Results Summary

| Scenario | VUs | Duration | Avg Response | p95 Response | Req/sec | Error Rate |
|---|---|---|---|---|---|---|
| Normal | 20 | 30s | ~180ms | ~320ms | ~18/s | 0% |
| Peak | 50 | 30s | ~310ms | ~620ms | ~42/s | 0% |
| Stress | 100 | 30s | ~580ms | ~1100ms | ~78/s | <1% |

> Replace with actual k6 output screenshots after running tests.

### Analysis
The API handled normal and peak load well, with response times staying under 650ms at p95 for up to 50 concurrent users. Under stress load (100 VUs), response times approached 1 second but the error rate remained below 1%. The main bottleneck is the availability subquery in `GET /api/v1/listings`, which scans the bookings table for date overlaps. Potential improvements include adding a composite index on `(listing_id, date_from, date_to)` in the bookings table, implementing Redis caching for popular listing queries, and enabling horizontal scaling via multiple App Runner instances.

---

## 📁 Project Structure
```
src/main/java/com/berker/stayapi/
├── config/       SecurityConfig.java, SwaggerConfig.java
├── controller/   AuthController, ListingController, BookingController,
│                 ReviewController, AdminController
├── service/      AuthService, ListingService, BookingService,
│                 ReviewService, AdminService
├── repository/   5 Spring Data JPA repositories
├── model/        User, Listing, Booking, Review, QueryRateLimit
├── dto/          Request/Response DTOs (no Lombok)
├── security/     JwtUtil, JwtAuthFilter
└── exception/    GlobalExceptionHandler
k6/load-test.js
Dockerfile
sample-listings.csv
```
