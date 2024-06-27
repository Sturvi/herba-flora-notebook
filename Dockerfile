# Use Eclipse Temurin as the base image
FROM eclipse-temurin:17-jre

# Проверка соединения с google.com
RUN ping -c 1 google.com

# Copy the JAR file to the container
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
