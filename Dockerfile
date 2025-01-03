FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY src ./src
COPY pom.xml ./
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/task_service-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
