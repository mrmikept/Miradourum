# -------- Build stage --------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# -------- Runtime stage --------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/miradourum-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
