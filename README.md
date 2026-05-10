#  Finance360 — Personal Finance Tracker

A full-stack personal finance web application built with **Spring Boot** backend 
and a **vanilla JavaScript** dashboard frontend.

##  Features
- JWT-based user authentication (register/login)
- Add, edit, delete income & expense transactions
- Savings goal tracking with progress bars
- Recurring transaction support (daily/weekly/monthly)
- Live analytics — expense pie chart, income vs expense bar, savings trend line
- Monthly financial reports with top category insights
- Per-user data isolation — users only see their own data

##  Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3, Spring Security, JPA/Hibernate |
| Authentication | JWT (jjwt 0.12.5) |
| Database | H2 (dev) / PostgreSQL (prod) |
| Frontend | HTML, CSS, JavaScript, Chart.js |
| Deployment | Render (backend) + Netlify (frontend) |

## 📂 Project Structure

```text
finance360/
├── src/main/java/com/myfinance/finance360/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # JPA queries (user-scoped)
│   ├── model/          # JPA entities
│   ├── dto/            # TransactionDTO, GoalDTO
│   ├── security/       # JwtService, JwtFilter
│   └── config/         # SecurityConfig, CORS
│
├── src/main/resources/
│   └── application.yml # Dev + Prod profiles
│
└── frontend/
    └── index.html      # Single-page dashboard
```
## Run Locally

### Prerequisites
- Java 17+
- Maven

### Steps
```bash
# Clone the repo
git clone https://github.com/sandhiyaai/finance360.git
cd finance360

# Run backend (dev profile — H2 file database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Open frontend
# Just open frontend/index.html in your browser
```

### API Base URL
http://localhost:8080
## API Endpoints

### Auth (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Create new account |
| POST | `/auth/login` | Login, get JWT token |

### Transactions (Protected)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/transactions` | Get all transactions |
| POST | `/transactions` | Add transaction |
| PUT | `/transactions/{id}` | Update transaction |
| DELETE | `/transactions/{id}` | Delete transaction |
| GET | `/transactions/summary` | Income/expense/balance |
| GET | `/transactions/category-summary` | By category |

### Goals (Protected)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/goals` | Get all goals |
| POST | `/goals` | Create goal |
| DELETE | `/goals/{id}` | Delete goal |

### Insights (Protected)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/insights` | Financial insights |
| GET | `/insights/monthly` | Monthly report |

## Key Technical Decisions

**DTOs over raw entities** — `TransactionDTO` and `GoalDTO` prevent 
Jackson circular serialization (Transaction → User → Transaction loop).

**User-scoped repositories** — Every query includes the authenticated user:
`findByUserAndType(user, type)` — prevents cross-user data access.

**JWT stateless auth** — No server-side sessions. Token validated 
on every request via `JwtAuthenticationFilter`.

**File-based H2 in dev** — `jdbc:h2:file:./financedb` persists data 
across restarts unlike in-memory H2.

## 👩‍💻 Author
**Sandhiya S** — [LinkedIn](www.linkedin.com/in/sandhiya-s-7959bb2b2)
| [GitHub](https://github.com/sandhiyaai)
