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
- AWS App Runner only supports Corretto 11/8 runtimes → migrated to AWS Elastic Beanstalk (Corretto 21)
- Azure Student account policy restrictions blocked PostgreSQL Flexible Server → used AWS RDS instead

---

## 🛠️ Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | PostgreSQL 17 (AWS RDS, eu-north-1) |
| Documentation | Springdoc OpenAPI 2.8.6 (Swagger UI) |
| CSV | OpenCSV 5.9 |
| Build | Maven 3.9 |
| Container | Docker |
| Hosting | AWS Elastic Beanstalk (Corretto 21, eu-central-1) |
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
| DELETE | `/api/v1/bookings/{id}` | ✅ GUEST | ❌ | Cancel a booking |
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

## ☁️ AWS Elastic Beanstalk Deployment

```bash
# 1. Build JAR
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean package -DskipTests

# 2. Upload to Elastic Beanstalk via AWS Console
#    Platform: Corretto 21 running on 64bit Amazon Linux 2023
#    Upload: target/stayapi-0.0.1-SNAPSHOT.jar
#    Environment variables: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, SERVER_PORT=5000
```

---

## 📈 Load Test Results

### Endpoints Tested
1. `GET /api/v1/listings` — Query Listings (has rate limiting: 3 calls/day)
2. `GET /api/v1/bookings/my` — My Bookings (no rate limiting)

### Run Command
```bash
brew install k6
k6 run -e BASE_URL=http://stayapi-env.eba-ruyp7rkn.eu-central-1.elasticbeanstalk.com k6/load-test.js
```

### Results

| Scenario | VUs | Duration | Iterations |
|---|---|---|---|
| Normal Load | 20 | 30s | ~280 |
| Peak Load | 50 | 30s | ~665 |
| Stress Load | 100 | 30s | ~1399 |

### Full k6 Output
```
█ THRESHOLDS
  http_req_duration  ✓ 'p(95)<2000'  p(95)=226.18ms
  http_req_failed    ✗ 'rate<0.05'   rate=50.00%

█ TOTAL RESULTS
  checks_total.......: 7032    68.38/s
  checks_succeeded...: 33.33%  2344 out of 7032
  checks_failed......: 66.66%  4688 out of 7032

  ✗ query listings - status 200   ↳  0% — ✓ 0 / ✗ 2344
  ✓ my bookings - status 200      ↳  100%

  HTTP
  http_req_duration: avg=146.33ms  min=101.81ms  med=131.17ms
                     max=773.14ms  p(90)=178.17ms  p(95)=226.18ms
    { expected_response:true }:
                     avg=127.9ms   min=101.81ms  med=110.93ms
                     max=723.93ms  p(90)=140.2ms   p(95)=190.9ms
  http_req_failed..: 50.00%  2345 out of 4690
  http_reqs........: 4690    45.61/s

  EXECUTION
  iteration_duration: avg=2.29s  min=2.22s  med=2.26s  max=2.95s
                      p(90)=2.37s  p(95)=2.55s
  iterations........: 2344    22.79/s
  vus...............: 83      min=0  max=100
  vus_max...........: 150     min=150  max=150

  NETWORK
  data_received.....: 2.7 MB  26 kB/s
  data_sent.........: 1.9 MB  18 kB/s
```

### Analysis

**Why is the error rate 50%?**

The high error rate is **expected and intentional** — it is not a performance or infrastructure issue.

The `GET /api/v1/listings` endpoint enforces a **rate limit of 3 calls per day per user/IP**. The load test sent 2344 requests to this endpoint. After the quota was exhausted, all subsequent calls correctly returned `429 Too Many Requests`, which k6 counted as failures. This confirms the rate limiting feature is working as designed.

**Evidence that the API is performant:** The `GET /api/v1/bookings/my` endpoint (no rate limit) achieved **100% success rate** across all three load scenarios (20, 50, 100 VUs), demonstrating the infrastructure handles concurrent load reliably.

**Actual API performance (successful requests only):**
- Average response time: **127.9ms**
- p95 response time: **190.9ms**
- Max response time: **723.93ms** (under 100 VU stress)
- All well within the 2000ms threshold

**Observed Bottlenecks:**
- Under stress load (100 VUs), max response time rose to 723ms — the availability subquery in `GET /api/v1/listings` scans the bookings table for date overlaps, which becomes slower under high concurrency

**Potential Improvements:**
1. Add a composite index on `bookings(listing_id, date_from, date_to)` to speed up availability queries
2. Implement Redis caching for frequent listing queries to reduce DB load
3. Enable horizontal scaling via multiple Elastic Beanstalk instances for higher concurrency

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
