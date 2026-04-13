FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

RUN mvn -B dependency:go-offline

COPY src src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV PORT=8080
ENV APP_UPLOAD_DIR=/data/uploads

RUN mkdir -p /data/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --server.port=${PORT}"]