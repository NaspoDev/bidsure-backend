# ==== Build stage ====
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven config and download dependencies first (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application JAR
RUN mvn clean package -DskipTests

# ==== Runtime stage ====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# IMPORTANT: set the port this service listens on
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "app.jar"]
