# Notification-Service

A high-performance **Notification Service** built with **Java Spring Boot** that handles real-time notifications using **Kafka**, **STOMP WebSockets**, and **Redis**. This service is designed to provide scalable, asynchronous, and reliable notifications for microservices architectures.

---

## **Key Features**

- **Real-time Notifications:** Push notifications to clients using STOMP over WebSocket.  
- **Asynchronous Messaging:** Uses Kafka for decoupled, reliable message streaming between services.  
- **Caching & Persistence:** Redis is used for storing active sessions, message queues, and caching.  
- **Microservices Friendly:** Easily integrates with other Spring Boot microservices.  
- **Scalable & Resilient:** Handles high throughput of notifications without blocking.

---

## **Technologies Used**

- **Java 21 / Spring Boot 3.x** – Framework for building microservices.  
- **Apache Kafka** – Message broker for asynchronous communication.  
- **STOMP over WebSocket** – Real-time client notifications.  
- **Redis** – In-memory data store for caching and session management.  
- **Maven / Gradle** – Project build and dependency management.  

---

## **Architecture Overview**

1. **Producer Service** sends notification events to Kafka topics.  
2. **Notification-Service** consumes events from Kafka asynchronously.  
3. Messages are pushed to clients via **STOMP WebSockets**.  
4. **Redis** stores active client sessions and message states for fast access.

