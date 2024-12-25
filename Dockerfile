# Stage 1: Build the application
FROM amazoncorretto:17 AS build

# Set the working directory
WORKDIR /app

# Copy all files from the current directory to the container
COPY . .

# Grant execution permission to gradlew
RUN chmod +x ./gradlew

# Build the application using gradlew
RUN ./gradlew clean bootJar --no-daemon

# Stage 2: Create the runtime image
FROM amazoncorretto:17

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application with environment variable for profile
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]