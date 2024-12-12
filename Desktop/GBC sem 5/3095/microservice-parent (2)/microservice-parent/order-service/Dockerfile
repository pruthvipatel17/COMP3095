# Stage 1: Build
FROM gradle:8-jdk22-alpine AS builder

# Copy the entire multi-module project
COPY --chown=gradle:gradle .. /home/gradle/src

# Set working directory
WORKDIR /home/gradle/src

# Build the specific service
RUN gradle :order-service:bootJar -x test

# Stage 2: Runtime
FROM openjdk:22-jdk

# Application directory
RUN mkdir /app

# Copy the built JAR from the builder
COPY --from=builder /home/gradle/src/order-service/build/libs/*.jar /app/order-service.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "/app/order-service.jar"]
