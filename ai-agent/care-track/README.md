# CareTrack

## Prerequisites
- Java 21
- Maven 3.9 or higher
- Docker Compose
- An IDE like IntelliJ IDEA (optional)

## Getting Started

Run docker compose to start all the dependencies, including Postgres and Axon Server:

```bash
docker compose up -d
```

Now, you can run the application using Maven:

```bash
mvn spring-boot:run
```

Alternatively, you can run the application from your IDE by running the main class.

## Using the Application
The application also exposes a REST API, which you can explore using tools like Postman or curl.
The events are stored in Axon Server, which you can access at [http://localhost:8024](http://localhost:8024).

## Useful resources

You can access a lot of resources through the [Axoniq Platform](https://platform.axoniq.io/). This includes:

- Extensive Documentation
- Axoniq Academy with free courses
- AI-powered Development Agent to create your applications faster
- Community Forum to ask questions and share knowledge
- Monitor your applications
- And much more!