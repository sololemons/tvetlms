# TVET LMS


This repository contains the source code for a Technical and Vocational Education and Training (TVET) Learning Management System (LMS). It is designed using a microservices architecture, with various services handling distinct functionalities of the platform.

## Architecture

The system is composed of several independent microservices that communicate with each other through a combination of REST APIs and asynchronous messaging with RabbitMQ. A Netflix Eureka server handles service discovery, and an API Gateway provides a single entry point for all client requests.

### Core Components

*   **Eureka Server**: Acts as the service registry where all other microservices register themselves.
*   **API Gateway**: The single entry point for all incoming API requests. It routes traffic to the appropriate downstream service.
*   **Admin Service**: Manages administrative functions, such as creating student and staff accounts.
*   **Student Service**: Handles all student-related operations, including registration, authentication, profile management, and assignment submission.
*   **Staff Service**: Manages staff-related operations, including authentication, assignment creation, and viewing student submissions.
*   **Course Management Service**: Responsible for creating, managing, and retrieving course content, including overviews and modules.
*   **Chat Service**: Provides real-time chat functionality between users via WebSockets.
*   **Grade Service**: A service designed to handle the grading of student submissions, interacting with other services via message queues.

## Technologies Used

*   **Backend**: Java 21, Spring Boot 3
*   **Service Discovery**: Spring Cloud Netflix Eureka
*   **API Gateway**: Spring Cloud Gateway
*   **Database**: MySQL
*   **Data Access**: Spring Data JPA / Hibernate
*   **Authentication**: Spring Security, JSON Web Tokens (JWT)
*   **Messaging**: Spring AMQP with RabbitMQ for asynchronous communication
*   **Real-time Communication**: Spring WebSocket (STOMP) for the chat service
*   **Distributed Tracing**: Micrometer and Zipkin
*   **Build Tool**: Apache Maven

## Services Breakdown

Each service is a standalone Spring Boot application configured to run on a specific port and connect to its own database.

| Service                     | Port | Database Name                 | Description                                                                                              |
| --------------------------- | ---- | ----------------------------- | -------------------------------------------------------------------------------------------------------- |
| **Eureka Server**           | 8761 | N/A                           | Service registry for the microservices.                                                                  |
| **API Gateway**             | 8080 | N/A                           | Routes requests to other microservices.                                                                  |
| **Student Service**         | 8081 | `tvetstudentservice`          | Manages student accounts, authentication, and assignment submissions.                                    |
| **Staff Service**           | 8082 | `tvetstaffservice`            | Manages staff accounts, assignment creation, and submission viewing.                                     |
| **Admin Service**           | 8083 | `tvetadminservice`            | Handles administrative tasks like user creation.                                                         |
| **Grade Service**           | 8084 | `tvetgradeservice`            | Processes grading requests for assignments.                                                              |
| **Chat Service**            | 8085 | `tvetchatservice`             | Facilitates real-time messaging between users.                                                           |
| **Course Management Service** | 8086 | `tvetcoursemanagementservice` | Manages all course and module content.                                                                   |

### Asynchronous Communication (RabbitMQ)

The services leverage RabbitMQ for decoupling and asynchronous processing:

*   `add_student_queue`: Used by the **Admin Service** to send new student details to the **Student Service**.
*   `add_staff_queue`: Used by the **Admin Service** to send new staff details to the **Staff Service**.
*   `add_assignment_queue`: Used by the **Student Service** to send assignment submissions to the **Staff Service**.
*   `grading_request_queue` / `grading_response_queue`: Used for communication between the **Staff Service** and **Grade Service** for grading assignments.
*   `check_role_queue_*`: Used to check user roles across different services.

## Setup and Installation

To run this project locally, you will need the following installed:

*   JDK 21
*   Apache Maven
*   MySQL Server
*   RabbitMQ Server
*   Zipkin (for distributed tracing)

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sololemons/tvetlms.git
    cd tvetlms
    ```

2.  **Database Setup:**
    Create a separate MySQL database for each service that requires one (see table above).
    ```sql
    CREATE DATABASE tvetstudentservice;
    CREATE DATABASE tvetstaffservice;
    CREATE DATABASE tvetadminservice;
    CREATE DATABASE tvetgradeservice;
    CREATE DATABASE tvetchatservice;
    CREATE DATABASE tvetcoursemanagementservice;
    ```
    Update the `application.properties` file in each service's `src/main/resources` directory with your MySQL username and password.

3.  **Start Dependencies:**
    Ensure your MySQL and RabbitMQ servers are running on their default ports.

4.  **Build and Run Services:**
    Build each microservice using Maven. Navigate to each service's root directory (e.g., `eurekaserver`, `apigateway`, etc.) and run:
    ```bash
    ./mvnw clean install
    ```
    Start the services in the following order. You can run them from your IDE or using the command line:

    ```bash
    # In the service's root directory
    ./mvnw spring-boot:run
    ```

    **Startup Order:**
    1.  `eurekaserver`
    2.  `apigateway`
    3.  All other services (order does not matter):
        *   `adminservice`
        *   `studentservice`
        *   `staffservice`
        *   `coursemanagementservice`
        *   `chatservice`
        *   `gradeservice`

Once all services are running, the application will be accessible through the API Gateway, which defaults to port `8080`.
