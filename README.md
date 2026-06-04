## 🚀 Запуск проекта

### Backend (Spring)

#### Локальный запуск
```bash

# Запустить окружение
cd infra

mvn clean package -DskipTests

docker compose up -d

Поднимутся контейнеры для приложения и  postgress

Открыть http://localhost:8080

# собрать проект для разработки
cd infra

# Модуль витрины
mvn clean package -DskipTests

docker compose up -d db redis keycloak

cd ../my-marker-app/shop

mvn spring-boot:run -Dspring-boot.run.profiles=dev

Открыть http://localhost:8080

# Модуль платежей
mvn clean package -DskipTests

docker compose up -d db redis

cd ../my-marker-app/payment

mvn spring-boot:run -Dspring-boot.run.profiles=dev

Открыть http://localhost:8081

# Запустить тесты
mvn test


### Описание 

в кейлоке нкообходимо завести пользователя под каким нужно будет авторизоваться 