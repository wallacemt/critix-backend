FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY ../.env .

RUN mvn clean package -DskipTests


FROM azul/zulu-openjdk:17

WORKDIR /app

COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY ../.env .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]