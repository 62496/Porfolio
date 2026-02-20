# Booksta – Fullstack Online Library Platform

## Overview

Booksta is a full-stack web application that allows users to browse books, track their reading progress, follow authors and series, interact with other readers, and manage personalized collections.

The project is built using a modern React frontend and a secured Spring Boot REST API with OAuth2 authentication and JWT-based authorization.

This project demonstrates full-stack architecture design, authentication security, role-based access control, and cloud deployment.

---

## Live Demo

Frontend (Firebase Hosting):  
https://booksta-859bc.web.app/

Backend API (Railway):  
https://booksta-production.up.railway.app

---

## Architecture

The application follows a classic full-stack architecture:

React (Frontend)  
↓ REST API (Axios)  
Spring Boot (Backend)  
↓  
H2 Database (JPA / Hibernate)

### Backend Architecture

- REST Controllers
- Service Layer (Business Logic)
- Repository Layer (Spring Data JPA)
- Entity-based data model
- JWT authentication filter
- Role-based access control


## Authentication & Security

- Google OAuth2 login
- JWT token generation
- Refresh token mechanism
- Spring Security configuration
- Role-based authorization (User, Author, Librarian, Seller)
- Secured REST endpoints

---

## Core Features

### User Features
- Browse and search books
- Filter by author, genre, publication year
- Track reading progress
- Create reading wishlists
- Rate and review books
- Follow authors and book series
- Create public or private collections
- Private messaging with other users

### Author Features
- Manage published books
- Share updates with readers
- Track engagement

### Librarian Features
- Manage catalog data
- Handle reported errors

### Seller Features
- Manage stock and book availability

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- H2 (demo database)

### Frontend
- React 18
- React Router
- Axios
- Tailwind CSS

### Tools
- Git & GitHub
- Postman
- Firebase Hosting
- Railway Deployment

---

## Database

- H2 in-memory database (demo purpose)
- Preloaded data via SQL script
- JPA entity relationships (OneToMany, ManyToOne, etc.)

---

## Deployment

### Frontend
Deployed using Firebase Hosting.

### Backend
Deployed on Railway with automatic deployment from GitHub.

Environment variables are configured directly in Railway.

---

## Run Locally

### Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend runs at:
👉 [http://localhost:8081](http://localhost:8080)

---

###  Frontend (React)

```bash
cd frontend
npm install
npm start
```

Frontend runs at:
 [http://localhost:3000](http://localhost:3000)

---
