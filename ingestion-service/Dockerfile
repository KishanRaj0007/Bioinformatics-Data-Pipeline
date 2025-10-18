# Stage 1: Build the application using a full JDK
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy the Maven wrapper and project definition file
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .

# Download dependencies to leverage Docker layer caching
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src/ src/

# Package the application into a .jar file
RUN ./mvnw package -DskipTests

# Stage 2:
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the packaged .jar file from the 'build' stage
COPY --from=build /app/target/ingestion-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]