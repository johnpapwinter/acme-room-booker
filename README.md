# Room Booker API

A Spring Boot REST API for managing meeting room bookings and user authentication.

## Features

- User authentication and authorization with JWT
- Meeting room booking management
- User role management
- Search functionality for room availability

## API Endpoints

### Authentication

#### Register New User
```http
POST /api/auth/register
```
**Request Body:**
```json
{
  "name": "string",
  "email": "string",
  "username": "string",
  "password": "string",
  "confirmPassword": "string"
}
```
**Response:** Returns the created user ID

#### Login
```http
POST /api/auth/login
```
**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```
**Response:**
```json
{
  "token": "string",
  "username": "string",
  "email": "string",
  "id": "number",
  "roles": ["string"]
}
```

### User Management

#### Get All Users
```http
GET /api/user/get-all?page=0&size=10
```
**Response:** Paginated list of users with their details

#### Toggle User Role
```http
GET /api/user/toggle-role/{userId}
```
**Response:** Updated user ID

### Meeting Management

#### Get All Meetings
```http
GET /api/meetings/get-all?page=0&size=10
```
**Response:** Paginated list of meetings

#### Book Meeting
```http
POST /api/meetings/book
```
**Request Body:**
```json
{
  "meetingRoom": "enum",
  "bookedBy": "string (email)",
  "bookingDate": "YYYY-MM-DD",
  "startTime": "HH:mm:ss",
  "endTime": "HH:mm:ss"
}
```
**Response:** Created meeting ID

#### Cancel Meeting
```http
GET /api/meetings/cancel/{id}
```
**Response:** Cancelled meeting details

#### Search Meetings
```http
POST /api/meetings/search?page=0&size=10
```
**Request Body:**
```json
{
  "meetingRoom": "enum",
  "bookingDate": "YYYY-MM-DD"
}
```
**Response:** Paginated list of meetings matching the search criteria

## Data Models

### Meeting DTO
```json
{
  "id": "number",
  "meetingRoom": "enum",
  "bookedBy": "string (email)",
  "bookingDate": "YYYY-MM-DD",
  "startTime": "HH:mm:ss",
  "endTime": "HH:mm:ss"
}
```

### User DTO
```json
{
  "name": "string",
  "email": "string",
  "username": "string",
  "role": "enum"
}
```

## Validation

The API includes comprehensive validation for all endpoints:
- Required field validation
- Email format validation
- Password matching validation for registration
- Date and time validation for meetings

## Security

- JWT-based authentication
- Role-based access control
- Secure password handling
- Protected endpoints requiring authentication

## Error Handling

The API uses standardized error messages with unique error codes:
- ARB1001: Room is required
- ARB1002: Meeting organizer is required
- ARB1003: Invalid email address
- ARB1004: Date is required
- ARB1005: Starting time is required
- ARB1006: End time is required
- ARB1007: Name is required
- ARB1008: Email is required
- ARB1009: Username is required
- ARB1010: Password is required
- ARB1012: Confirmation password required

## Prerequisites

- Java 17 or higher
- Spring Boot 3.x
- Maven/Gradle for dependency management
- PostgreSQL/MySQL database (configure in application.properties)

## Getting Started

1. Clone the repository
2. Configure your database connection in `application.properties`
3. Run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The API will be available at `http://localhost:8080`

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request