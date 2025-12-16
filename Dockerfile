# Stage 1: Build Frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Copy frontend build to Spring public directory
COPY --from=frontend-build /app/dist ./src/main/resources/public
RUN mvn clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
