# --- Bước 1: Build ứng dụng với Maven và JDK 21 ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build gói JAR, bỏ qua test để tiết kiệm thời gian
RUN mvn clean package -DskipTests

# --- Bước 2: Chạy ứng dụng với JDK 21 rút gọn (Alpine) ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy file .jar đã build ở bước 1 sang
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]