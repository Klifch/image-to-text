FROM eclipse-temurin:17.0.3_7-jdk-jammy

WORKDIR /app

COPY build/libs/api_image_processor-0.0.1-SNAPSHOT.jar app2.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app2.jar"]
