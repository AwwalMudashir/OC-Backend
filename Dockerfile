FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pc_backend/pom.xml ./pom.xml
COPY pc_backend/.mvn ./.mvn
COPY pc_backend/mvnw ./mvnw
COPY pc_backend/mvnw.cmd ./mvnw.cmd

RUN mvn -B dependency:go-offline

COPY pc_backend/src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV PORT=8080
ENV APP_UPLOAD_DIR=/data/uploads

RUN mkdir -p /data/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --server.port=${PORT}"]