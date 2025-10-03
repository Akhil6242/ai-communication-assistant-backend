FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN echo "=== POM.XML COPIED ==="
COPY src ./src
RUN echo "=== SOURCE COPIED ==="
RUN ls -la /app/
RUN ls -la /app/src/
RUN echo "=== STARTING MAVEN BUILD ==="
RUN mvn clean package -DskipTests
RUN echo "=== MAVEN BUILD COMPLETED ==="
RUN echo "=== CHECKING TARGET DIRECTORY ==="
RUN ls -la /app/
RUN ls -la /app/target/ || echo "TARGET DIRECTORY NOT FOUND"

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
