# Stage 1: Build the application
FROM maven:3-openjdk-17 AS build
COPY . .
RUN mvn clean install

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/TOEIC-Rise-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
