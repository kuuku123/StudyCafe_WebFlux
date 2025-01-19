# Use a lightweight JDK runtime as the base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/webflux-notification.jar /app/webflux-notification.jar

# Expose the application port
EXPOSE 8082

# Run the application with the prod profile
CMD ["java", "-jar", "webflux-notification.jar", "--spring.profiles.active=prod"]

