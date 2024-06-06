FROM eclipse-temurin:17.0.3_7-jdk-jammy

WORKDIR /app

COPY build/libs/imagetotextapi-0.0.1-SNAPSHOT.jar app1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app1.jar"]
