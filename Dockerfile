# Stage 1: Build source code bằng Maven và OpenJDK 24
FROM maven:3.9.9-amazoncorretto-24-alpine AS build
WORKDIR /app

# Copy file pom.xml trước để tận dụng cache các dependency của Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn còn lại và build ra file JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Tạo môi trường chạy tinh gọn (Runtime) với Java 24 JRE
FROM amazoncorretto:24-alpine
WORKDIR /app

# Copy file JAR đã build từ stage trước sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 để Render map với môi trường bên ngoài
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]