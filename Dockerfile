# =========================================================================
# Multi-Stage Dockerfile for Open Source Project Portal
# Stage 1: Build stage with Maven and Eclipse Temurin JDK 21
# Stage 2: Secure, lightweight Eclipse Temurin JRE 21 runtime with non-root user
# =========================================================================

# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy pom.xml and cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile package
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Production-grade lightweight runtime
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Create unprivileged system group and user for security compliance
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled JAR from builder stage
COPY --from=builder /app/target/open-source-project-portal-*.jar app.jar

# Enforce non-root ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser:appgroup

# Expose Spring Boot port
EXPOSE 8080

# Configure JVM flags optimized for container environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Launch Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
