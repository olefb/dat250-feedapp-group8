# FeedApp - DAT250 Software Technologies Project (Group 8)

Welcome to the FeedApp project! This repository contains a full-stack polling application designed to demonstrate the integration of a modern, containerized technology stack. The project serves as both a functional prototype and a formal technology assessment of **Apache Kafka** as a featured technology, compared against a **RabbitMQ** baseline.

## Key Features

*   **Full-Stack Architecture:** A reactive Svelte frontend communicating with a stateless Spring Boot 3 backend.
*   **JWT-Based Security:** Secure user registration and authentication using JSON Web Tokens.
*   **Asynchronous Messaging:** Utilizes Kafka (or RabbitMQ) to decouple high-volume vote ingestion from database persistence.
*   **Polyglot Persistence:** Employs H2 for relational data integrity and Valkey (Redis) for high-performance caching of poll results.
*   **Containerized Deployment:** The entire application and its services are orchestrated via Docker Compose for easy, one-command setup.

## Technology Stack

| Category | Technology | Role in Project |
| :--- | :--- | :--- |
| **Backend** | Java 25 & Spring Boot 3 | Core application framework and dependency injection. |
| | Spring Web MVC | Exposes the stateless REST API. |
| | Spring Security | Handles JWT-based authentication and authorization. |
| **Frontend** | Svelte & SvelteKit | A reactive, component-based Single Page Application (SPA). |
| **Database & Cache**| H2 (File-based) | Relational database for primary data persistence. |
| | Valkey | In-memory cache for optimizing read-heavy operations (vote counts).|
| | JPA & Hibernate | Object-Relational Mapping (ORM) layer. |
| **Messaging** | **Apache Kafka** (Featured)| High-throughput, durable event streaming for vote processing. |
| | RabbitMQ (Baseline) | Traditional message broker used for comparison. |
| **DevOps** | Docker & Docker Compose | Containerization and orchestration of the entire stack. |
| | GitHub Actions | Continuous Integration pipeline for building and pushing the public Docker image. |

## Getting Started

This guide explains how to configure and run the application.

### Prerequisites

*   Docker
*   Docker Compose

### 1. Configuration

The application is configured using a single `.env` file in the project root.

1.  Create a file named `.env` in the root of the project.
2.  Use the template below:

    ```env
    # A long, random string to secure JWTs.
    # You can generate one with: openssl rand -base64 32
    JWT_SECRET=your-super-secret-key-goes-here
    
    # The active messaging profile. Switch between 'kafka' and 'rabbitmq'.
    SPRING_PROFILES_ACTIVE=kafka
    ```

### 2. Running the Application

There are two methods to run the project, depending on your goal.

#### Option A: Integrated Build (Recommended for Development)

This method builds the application from the local source code and runs all services together.

```bash
docker compose up --build -d
```

#### Option B: Standalone Image (For Delivery Verification)

This method pulls the pre-built public image from Docker Hub and connects it to the infrastructure, simulating how an external user would run it.

1.  **Start the infrastructure services first:**
    ```bash
    docker compose up -d kafka valkey rabbitmq
    ```

2.  **Run the application container manually.**
    *(Note: Ensure your network is named `dat250-feedapp-group8_default`. Check with `docker network ls` if it differs.)*
    ```bash
    docker run --rm --network dat250-feedapp-group8_default -p 8080:8080 \
      -e JWT_SECRET=${JWT_SECRET} \
      -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
      -e VALKEY_HOST="valkey" \
      -e SPRING_KAFKA_BOOTSTRAP_SERVERS="kafka:9092" \
      -e SPRING_RABBITMQ_HOST="rabbitmq" \
      --name feedapp-manual \
      olefb/feedapp:latest
    ```

### 3. Exploring the System

Once running, the following services are available:

| Service | URL | Credentials |
| :--- | :--- | :--- |
| **FeedApp Frontend** | `http://localhost:8080` | N/A |
| **H2 Database Console** | `http://localhost:8080/h2-console` | User: `admin`, Pass: (none) |
| **Kafka UI** | `http://localhost:8081` | N/A |
| **RabbitMQ Management** | `http://localhost:15672` | User: `guest`, Pass: `guest` |
