# Needle flow
A Spring Boot application for managing an embroidery thread inventory, designers, and embroidery designs. It tracks 
thread stock (skeins/bobbins), links threads to specific designs with required quantities, and automatically calculates 
a shopping list across multiple selected designs.

## Tech Stack

- **Java 21**, **Spring Boot 4** (Web, Data JPA, Validation)
- **PostgreSQL 17**
- **Maven**
- **Docker / docker-compose** — for the database
- Frontend — a lightweight Vue 3 SPA (CDN, no build step), styled with Tailwind CSS

## Features

- Full CRUD for embroidery designs, designers, thread manufacturers, and threads
- Stock tracking (skeins/bobbins) with the ability to add stock or set an exact quantity
- Linking threads to a design with the required amount of meters
- Automatic shopping list calculation across several selected designs at once
- Pagination, search by code/name, and manufacturer filtering on the threads list
- Image upload for a design's embroidery pattern

## Screenshots

![Design tab.png](.docs%2FDesign%20tab.png)
![Thread tab.png](.docs%2FThread%20tab.png)

## Getting Started

### Prerequisites
- JDK 21
- Docker (for the database)

### Steps

1. Clone the repository:
```bash
   git clone https://github.com/YuliaOsadchuk/needle-flow.git
   cd needle-flow
```

2. Start PostgreSQL with docker-compose:
```bash
   docker compose -f docker/docker-compose.yml up -d
```

3. Run the application:
```bash
   ./mvnw spring-boot:run
```

4. Open in your browser: http://localhost:8080

The database runs on port `5432`, database name is `needle_flow`, credentials are `username`/`password` 
(see `docker/docker-compose.yml` and `application.properties`).

## API

Base path: `/api/v1`

| Resource | Endpoint | Description |
|---|---|---|
| Designs | `GET /designs` | List all designs |
| | `GET /designs/{id}` | Get a design by id |
| | `POST /designs` | Create a design |
| | `PUT /designs/{id}` | Update a design |
| | `DELETE /designs/{id}` | Delete a design |
| | `POST /designs/{id}/image` | Upload a design's pattern image |
| | `POST /designs/shopping-list` | Calculate a shopping list for selected designs |
| Designers | `GET/POST/PUT/DELETE /designers[/{id}]` | Designer CRUD |
| Manufacturers | `GET/POST/PUT/DELETE /manufacturers[/{id}]` | Manufacturer CRUD |
| Threads | `GET /threads` | Paginated list (params: `page`, `size`, `search`, `manufacturerId`) |
| | `GET /threads/options` | Full thread list (used for dropdowns in forms) |
| | `GET/POST/PUT/DELETE /threads/{id}` | Thread CRUD |
| Inventory | `POST /inventory/add` | Add stock for a thread |
| | `POST /inventory/update` | Set an exact stock quantity |

## Project Structure

```
src/main/java/yosadchuk/needle/flow/
├── config/ # application configuration
├── controller/ # REST controllers
├── service/ # business logic
├── repository/ # Spring Data JPA repositories + Specifications
├── model/
│ ├── entity/ # JPA entities
│ └── dto/ # request/response DTOs
├── mapper/ # Entity <-> DTO mappers
└── exception/ # custom exceptions + global exception handler
```

## Running Tests

```bash
./mvnw test
```