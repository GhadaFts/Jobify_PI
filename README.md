# 🚀 Jobify — AI-Powered Career Assistant Platform

> An intelligent job platform combining traditional job portal features with AI-driven tools to help students and recent graduates stand out in the job market.

---

## 📌 Overview

**Jobify** is a full-stack career platform built with a **microservices architecture**, offering both a **web app (Angular)** and a **mobile app (Kotlin)**. It empowers job seekers with AI tools for CV generation, interview preparation, and smart job matching — all in one place.

### ✨ Key Features

| For Job Seekers | For Recruiters | For Admins |
|---|---|---|
| AI-powered CV generation & ATS correction | Post & manage job offers | Platform statistics |
| AI chatbot interview simulation | AI-ranked candidate lists | User management |
| One-click job applications | Interview scheduling | System monitoring dashboard |
| Smart job recommendations | Application management workflow | — |
| Job market analysis by country | Detailed application views | — |
| Application status tracking | — | — |

---

## 🛠️ Technology Stack

### Frontend
- **Angular** (TypeScript) + **TailwindCSS** + **RxJS** — Web application
- **Kotlin** — Mobile application

### Backend (Microservices)
| Service | Port | Tech | Responsibility |
|---|---|---|---|
| Gateway Service | 8888 | Spring Cloud | Routing, CORS, circuit breaker |
| Config Service | 8889 | Spring Cloud Config | Centralized config (GitHub) |
| Eureka Discovery | 8761 | Spring Eureka | Service registry & health monitoring |
| Auth Service | 3000 | NestJS + MongoDB | JWT, Keycloak integration |
| Interview Service | 8081 | Spring Boot + MySQL | Interview scheduling |
| Job Offer Service | 8082 | Spring Boot + MySQL | Job posting & company profiles |
| Application Service | 8083 | Spring Boot + MongoDB | CV upload & application tracking |
| AI Service | — | NestJS | CV correction, generation, job matching |
| Career Advice Service | 5000 | Flask | Job market analysis by country |

### Databases
- **MySQL** — via XAMPP (Job offers, interviews)
- **MongoDB** — Auth, applications

### Infrastructure
- **Keycloak** — Authentication & authorization (jobify-realm)
- **OpenFeign** — Inter-service communication
- **Grafana** — Real-time monitoring dashboards

---

## ⚙️ Getting Started

### Prerequisites

Make sure you have the following installed:
- Java 17+
- Node.js 18+
- Angular CLI
- Android Studio (for mobile)
- XAMPP (MySQL)
- MongoDB
- Keycloak server

---

### 1. Start Databases

**MySQL (via XAMPP):**
```bash
# Open XAMPP Control Panel and start:
# - Apache
# - MySQL
```

**MongoDB:**
```bash
# Start MongoDB service
mongod
```

---

### 2. Configure Keycloak

1. Start your Keycloak server
2. Create a realm named `jobify-realm`
3. Create a client and copy the **client secret**
4. Add the secret to your `.env` file:

```env
KEYCLOAK_CLIENT_SECRET=your_client_secret_here
KEYCLOAK_REALM=jobify-realm
KEYCLOAK_BASE_URL=http://localhost:8080
```

---

### 3. Start Backend Microservices

Start services **in this order**:

```bash
# 1. Config Service (must start first)
cd config-service && mvn spring-boot:run

# 2. Eureka Discovery
cd eureka-service && mvn spring-boot:run

# 3. Gateway Service
cd gateway-service && mvn spring-boot:run

# 4. Auth Service
cd auth-service && npm install && npm run start

# 5. Remaining services (any order)
cd job-offer-service && mvn spring-boot:run
cd application-service && mvn spring-boot:run
cd interview-service && mvn spring-boot:run

# 6. AI Service
cd ai-service && npm install && npm run start

# 7. Career Advice Service
cd career-advice-service && pip install -r requirements.txt && python app.py
```

---

### 4. Start Frontend

**Web (Angular):**
```bash
cd frontend && npm install && ng serve
# Visit: http://localhost:4200
```

**Mobile (Kotlin):**
```bash
# Open /mobile folder in Android Studio
# Run on emulator or connected device
```

---

## 🏗️ Architecture

```
Client (Angular / Kotlin)
        │
        ▼
  Gateway Service (:8888)
        │
        ├──► Auth Service (:3000) ──► Keycloak
        ├──► Job Offer Service (:8082) ──► MySQL
        ├──► Application Service (:8083) ──► MongoDB
        ├──► Interview Service (:8081) ──► MySQL
        ├──► AI Service ──► NestJS
        └──► Career Advice Service (:5000) ──► Flask

  Eureka Discovery (:8761) — monitors all services
  Config Service (:8889) — centralized config via GitHub
```

---

## 🤖 AI Features

- **CV Generation** — Auto-generates an ATS-optimized CV from the candidate's profile when applying to a job
- **CV Correction** — AI reviews and improves uploaded CVs
- **Smart Matching** — Ranks candidates by relevance to a job offer
- **Interview Chatbot** — AI recruiter simulation for interview practice
- **Career Advice** — Job market analysis and recommendations by country

---

## 📱 Platform Screenshots

> *Demo screenshots coming soon*

---

## 🔄 Agile Development — Scrum

| Sprint | Period | Focus |
|---|---|---|
| Sprint 1 | Oct 1 – Oct 22 | Core features & infrastructure |
| Sprint 2 | Oct 22 – Nov 12 | Authentication & application tracking |
| Sprint 3 | Nov 12 – Dec 3 | AI integration & optimization |

---

## 👥 Team

| Name | Role |
|---|---|
| Borhen Khadhroui | Developer |
| Mariem Bejaoui | Developer |
| Ghada Fatnassi | Developer |
| Tasnim Hajjeji | Developer |
| Mohamed Allani | Developer |

**Institution:** Institut Supérieur des Études Technologiques (ISET)
**Academic Year:** 2025/2026

---

## 📄 License

This project was developed as an academic integration project. All rights reserved © 2026.
