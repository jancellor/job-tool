# Build and test stage
FROM mcr.microsoft.com/playwright/java:v1.57.0-noble AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM mcr.microsoft.com/playwright/java:v1.57.0-noble
WORKDIR /app
COPY --from=build /app/target/job-tool-main-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
