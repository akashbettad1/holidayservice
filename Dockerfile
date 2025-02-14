# 1. Build Stage (Using Maven to build the application)
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven pom file and download the dependencies to cache them
COPY pom.xml .
RUN mvn clean install -DskipTests

# Copy the rest of the source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# List files in the target directory to verify JAR creation
RUN ls -al /app/target

# 2. Runtime Stage (Minimize image size and only use necessary dependencies)
FROM amazoncorretto:17

# Set the working directory for the application
WORKDIR /app

# Add metadata
LABEL maintainer="Your Name <your.email@example.com>"
LABEL version="1.0"
LABEL description="Holiday Service Application"

# Copy the JAR from the build stage
COPY --from=builder /app/target/holiday-service-0.0.1-SNAPSHOT.jar /app/holiday-service.jar

# Expose the port the app will run on
EXPOSE 8080

# Health check to monitor the container's health
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl --silent --fail http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/holiday-service.jar"]
