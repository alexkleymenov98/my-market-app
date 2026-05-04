## 🚀 Запуск проекта

### Backend (Spring)

#### Локальный запуск
```bash

# Запустить окружение
cd infra

docker compose up -d

Поднимутся контейнеры для приложения и  postgress

Открыть http://localhost:8080

# собрать проект для разработки
cd infra

docker compose up -d db

cd ../mymarket

mvn spring-boot:run -Dspring-boot.run.profiles=dev

Открыть http://localhost:8080

# Запустить тесты
mvn test