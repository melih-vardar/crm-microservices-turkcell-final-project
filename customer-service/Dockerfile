FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . /app/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build /app/target/customer-0.0.1-SNAPSHOT.jar /app/customer.jar

ENTRYPOINT ["java", "-jar", "/app/customer.jar"] 