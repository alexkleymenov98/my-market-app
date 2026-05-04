# Этап сборки с Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и собираем
COPY src src
RUN mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Используем Virtual Threads если нужно
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]