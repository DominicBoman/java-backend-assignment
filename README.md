# Assignment:
The task is to design and implement a backend service to control the
temperature in the building(s). The API should be REST based and the state of
the building should be persisted to any form of persistent storage. There is no
need for any frontend but we expect there to be a README.md file with build
directions and examples of how to invoke the REST API (e.g. curl).
The project should be implemented using Java. Feel free to use any 3rd party
library that you are comfortable with. Unit tests are expected and the
assignment will be assessed based on good programming practices and
design.

# Building Temperature Control API

A Spring Boot REST API for managing building temperature control systems. This application allows users to manage buildings and control temperature zones within those buildings.

## Features
- User authentication with JWT
- Building management (CRUD operations)
- Zone temperature control
- Multi-zone support per building
- Access control based on building ownership

## Technologies
- Java 17
- Spring Boot
- PostgreSQL
- Docker
- JWT for authentication

## Prerequisites
- Java 17
- Maven
- Docker and Docker Compose

## Building and Running

### Local Development
1. Clone the repository
2. Navigate to the api directory:
3. Build the application (or use included jar):
- bash
- mvn clean package
4. Start the application and database:
docker-compose up --build

The API will be available at `http://localhost:8080`

## API Testing Guide

### Authentication

#### Pre-configured Users
The system comes with two pre-configured users for testing:
- Username: `user1`, Password: `password`
- Username: `user2`, Password: `password`

#### Register New User (Optional)
bash
curl -X POST http://localhost:8080/api/v1/users/register \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-d '{
"username": "newuser",
"password": "password123",
"firstName": "John",
"lastName": "Doe"
}'

#### Get JWT Token
bash
curl -X POST http://localhost:8080/api/v1/users/authenticate \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-d '{
"username": "user1",
"password": "password"
}'

Save the returned token for subsequent requests.

### Building Management

#### Create a building:
bash
curl -X POST http://localhost:8080/api/v1/buildings \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{
"name": "Office Building",
"city": "New York",
"street": "123 Main St",
"postalCode": "10001"
}'

#### Get all buildings:
bash
curl -X GET http://localhost:8080/api/v1/buildings \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN"

#### Get specific building:
bash
curl -X GET http://localhost:8080/api/v1/buildings/1 \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN"

### Zone Management

#### Create a zone:
bash
curl -X POST http://localhost:8080/api/v1/buildings/1/zones \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{
"name": "Meeting Room",
"description": "Main meeting room",
"targetTemperature": 22.5
}'

#### Get zones in a building:
bash
curl -X GET http://localhost:8080/api/v1/buildings/1/zones \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN"

#### Update zone temperature:
bash
curl -X PATCH http://localhost:8080/api/v1/buildings/1/zones/1/target-temp \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{
"targetTemperature": 23.5
}'

#### Update all zones in a building:
bash
curl -X PUT http://localhost:8080/api/v1/buildings/1/zones/target-temp \
-H "Content-Type: application/json" \
-H "Accept: application/vnd.temperaturecontrol.v1+json" \
-H "Authorization: Bearer YOUR_JWT_TOKEN" \
-d '{
"targetTemperature": 23.5
}'