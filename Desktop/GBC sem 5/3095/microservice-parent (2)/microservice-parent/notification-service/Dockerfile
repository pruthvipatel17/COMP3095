# Stage 1: Build
FROM gradle:8-jdk22-alpine AS builder

# Copy the entire project for multi-module build
COPY --chown=gradle:gradle .. /home/gradle/src

# Set the working directory to the project root
WORKDIR /home/gradle/src

# Build the specific module
RUN gradle :notification-service:bootJar -x test

# Stage 2: Runtime
FROM openjdk:22-jdk

# Create the application directory
RUN mkdir /app

# Copy the built JAR from the builder stage
COPY --from=builder /home/gradle/src/notification-service/build/libs/*.jar /app/notification-service.jar

# Expose the port
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
