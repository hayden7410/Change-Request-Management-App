# Change Request Management System

## Overview

The **Change Request Management System** is a full-stack web application designed to standardize how organizational change requests are submitted, reviewed, routed, approved, and tracked.

The system provides a centralized platform for structured request intake, department-based routing, role-based access control, status tracking, approval decisions, comments, attachments, and AI-assisted request summary support.

This application is designed for organizations that need stronger visibility, consistency, and accountability over internal change requests.

---

## Business Problem

Change requests are often managed through informal or disconnected channels such as email, spreadsheets, chat messages, or general task tracking tools. This can create several operational issues:

- Incomplete or inconsistent request information
- Limited visibility into request status
- Manual follow-ups between requesters, reviewers, and approvers
- Unclear ownership and routing responsibilities
- Difficulty tracking approval decisions
- Lack of centralized history for comments, status changes, and supporting documents

The Change Request Management System addresses these issues by providing a structured workflow for submitting, reviewing, approving, and tracking requests from intake to closure.

---

## Key Features

### Structured Change Request Intake

Users can submit change requests through a standardized form that captures key business and technical information.

Supported request details include:

- Request title
- Request description
- Business problem
- Impacted system
- Requested outcome
- Urgency
- Priority
- Assigned department
- Supporting attachments

This ensures each request contains enough information for reviewers and approvers to assess the request properly.

### Role-Based Access Control

The system supports role-based access control to ensure users can only access the actions and request information relevant to their responsibilities.

Example roles may include:

- Requester
- Department Reviewer
- Approver
- Administrator

The RBAC design supports multiple roles per user and multiple permissions per role.

```text
User 1 --- * RoleAssignment * --- 1 Role

Role 1 --- * RolePermission * --- 1 Permission
```

This structure allows the system to support flexible permission management while preserving the ability to add metadata to role assignments and role-permission relationships in the future.

### Department-Based Routing

Change requests can be routed to the appropriate department based on request details, selected department, impacted system, or workflow rules.

This helps ensure that requests are reviewed by the correct business or technical team.

### Request Status Tracking

Each change request has a current status that represents its position in the workflow.

Example statuses include:

- Draft
- Submitted
- Under Review
- Approved
- Rejected
- Implementation Pending
- Implemented
- Closed

This provides visibility into the progress of each request.

### Status History

The system records status changes over time.

Status history can capture:

- Previous status
- New status
- User who made the change
- Timestamp of the change
- Reason or note related to the change

This supports traceability and auditability across the request lifecycle.

### Approval and Rejection Decisions

Approval and rejection actions are recorded separately from general status changes.

An approval decision can capture:

- Decision type
- Approver
- Decision date
- Reason or comment
- Related change request

This allows the system to distinguish between a request's workflow status and the business decision behind approval or rejection.

### Comments and Collaboration

Users can add comments to change requests to support clarification, review, and discussion.

Comment threads help centralize communication and reduce scattered conversations across email or chat platforms.

### Attachment Support

Users can upload supporting files to a change request.

The attachment design separates file metadata from physical file storage.

```text
AttachmentService
    -> Handles attachment business rules and upload coordination

FileStorageService
    -> Handles physical file storage

AttachmentRepository
    -> Stores attachment metadata in PostgreSQL
```

Attachment metadata may include:

- Original file name
- Stored file name
- File type
- File size
- Storage path
- Uploaded by
- Uploaded date
- Related change request

### AI-Assisted Summary Generation

The system is designed to support AI-assisted summary generation for change requests.

This feature can convert long request descriptions into structured summaries, including:

- Business problem summary
- Impacted system summary
- Urgency summary
- Requested outcome summary

Users can review and approve the generated summary before it is saved with the change request.

---

## Tech Stack

### Backend

- Java
- Spring Boot
- Maven
- Spring Data JPA
- PostgreSQL
- REST APIs

### Frontend

- React
- Next.js
- TypeScript

### Database

- PostgreSQL

### Planned Integrations

- External AI API for request summary generation
- Local or cloud-based file storage for attachments

---

## System Architecture

The application follows a layered architecture.

```text
React / Next.js Frontend
        ↓
REST API
        ↓
Spring Boot Controller Layer
        ↓
Service Layer
        ↓
Repository Layer
        ↓
PostgreSQL Database
```

### Backend Layer Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and API responses |
| DTO | Defines request and response data structures |
| Service | Contains business logic, workflow rules, and validation |
| Repository | Handles database access |
| Entity | Maps Java objects to PostgreSQL tables |
| Config / Security | Handles application configuration, authentication, and security setup |

---

## Core Domain Model

The system includes the following core domain entities:

- User
- Role
- Permission
- RoleAssignment
- RolePermission
- Department
- ChangeRequest
- Comment
- Attachment
- AISummary
- StatusHistory
- ApprovalDecision
- Notification

---

## Database Design

The application uses PostgreSQL as the primary relational database.

Core tables include:

```text
app_users
departments
roles
permissions
role_assignments
role_permissions
change_requests
comments
attachments
ai_summaries
status_history
approval_decisions
notifications
```

The RBAC model uses association entities:

```text
role_assignments
role_permissions
```

This supports:

- Multiple roles per user
- Multiple permissions per role
- Reusable permissions across roles
- Future metadata on role assignments and permission assignments

---

## Main Application Modules

### Authentication Module

Handles user login and identity verification.

Planned endpoints:

```http
POST /api/auth/login
GET  /api/auth/me
```

### User and Access Control Module

Handles users, roles, permissions, and role assignments.

Main responsibilities:

- Manage users
- Assign roles to users
- Assign permissions to roles
- Control access to requests and actions

### Change Request Module

Handles the main request lifecycle.

Main responsibilities:

- Create change requests
- View request lists
- View request details
- Update request information
- Route requests to departments
- Track current request status

Planned endpoints:

```http
POST   /api/change-requests
GET    /api/change-requests
GET    /api/change-requests/{id}
PATCH  /api/change-requests/{id}
```

### Comment Module

Handles discussion and clarification on change requests.

Planned endpoints:

```http
GET  /api/change-requests/{id}/comments
POST /api/change-requests/{id}/comments
```

### Workflow Module

Handles status transitions, approvals, rejections, and request lifecycle rules.

Planned endpoints:

```http
PATCH /api/change-requests/{id}/status
POST  /api/change-requests/{id}/approve
POST  /api/change-requests/{id}/reject
GET   /api/change-requests/{id}/history
```

### Attachment Module

Handles file uploads, downloads, and attachment metadata.

Planned endpoints:

```http
POST   /api/change-requests/{id}/attachments
GET    /api/change-requests/{id}/attachments
GET    /api/attachments/{id}/download
DELETE /api/attachments/{id}
```

### AI Summary Module

Handles AI-assisted request summary generation.

Planned endpoints:

```http
POST /api/ai/change-request-summary
POST /api/change-requests/{id}/summary/approve
```

---

## Environment Configuration

Database credentials and other sensitive values should be provided using environment variables rather than being committed to source control.

Example backend configuration:

```properties
spring.application.name=change-request-backend

spring.datasource.url=jdbc:postgresql://localhost:5432/change_request_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

### Set Environment Variable

#### Git Bash

```bash
export DB_PASSWORD="your_postgres_password"
```

#### PowerShell

```powershell
$env:DB_PASSWORD="your_postgres_password"
```

---

## Local Setup

### Prerequisites

- Java 21
- Maven
- PostgreSQL
- Node.js
- npm
- Git

### Database Setup

Create the PostgreSQL database:

```sql
CREATE DATABASE change_request_db;
```

To confirm tables after running the backend:

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;
```

### Run the Backend

From the project root:

```bash
cd Backend
mvn spring-boot:run
```

The backend should start on:

```text
http://localhost:8080
```

### Health Check

To verify that the backend is running:

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "UP"
}
```

### Run the Frontend

From the project root:

```bash
cd Frontend
npm install
npm run dev
```

The frontend should start on:

```text
http://localhost:3000
```

---

## Security Notes

The system is designed to support secure access control through:

- Email and password authentication
- Password hashing
- JWT-based authentication
- Role-based permissions
- Protected API endpoints
- Environment-based secret management

Sensitive values such as database passwords, JWT secrets, and API keys should not be committed to source control.

---

## Example Request Workflow

A typical request workflow may follow this path:

```text
Requester submits change request
        ↓
System assigns request to department
        ↓
Reviewer reviews request details
        ↓
Reviewer adds comments or requests clarification
        ↓
Approver approves or rejects request
        ↓
System records approval decision
        ↓
System updates request status
        ↓
Status history is stored
        ↓
Requester receives update
```

---

## Project Structure

```text
Change Request Management System
├── Backend
│   ├── src
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── Frontend
├── Database
├── Diagrams
├── Docs
├── Notes
├── Wireframes
├── .gitignore
└── README.md
```

---

## Author

Gia Hung Nguyen  
Business Systems Analysis Portfolio Project
