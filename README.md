# FeedApp - Group 8

## Technology Stack

* Frontend: Svelte
* Framework: Java with Spring Boot
* REST API: Spring Web MVC
* NoSQL DB/Cache: Valkey
* Relational database: H2
* ORM framework: Hibernate
* JPA: Hibernate & H2
* Message Broker: Kafka/RabbitMQ
* Container: Docker

## Featured Technology

Kafka

## Required .env Files
`.env` (for Docker Compose):  
`JWT_SECRET=[Enter JWT secret key here]`

`backend/.env` (for Spring Boot):  
`JWT_SECRET=[Enter JWT secret key here]`

`frontend/.env`:  
`VITE_API_BASE_URL=http://localhost:8080`

`frontend/.env.development`:  
`VITE_API_BASE_URL=http://localhost:8080`

`frontend/.env.production`:  
`VITE_API_BASE_URL=`

## How to Run

* Select between RabbitMQ and Kafka in `docker-compose.yml`
* Run with `docker-compose up`
* Open Svelte frontend at http://localhost:8080
* Open Kafka UI at http://localhost:8081
* Open RabbitMQ Management at http://localhost:15672
* K6 load testing scripts in root directory can be run using Grafana K6: `k6 run test-k6.js`