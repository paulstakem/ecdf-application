FROM azul/zulu-openjdk-alpine:21-jre

WORKDIR /app

# Create the volume for file storage (evidence attachments)
VOLUME /data/storage
ENV FILE_STORAGE_PATH=/data/storage

# Copy the built Spring Boot executable jar from the host machine
# Assumes the application has already been built (e.g., via `./gradlew build`)
COPY application/build/libs/application-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
