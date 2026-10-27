FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget \
    && addgroup -S adent \
    && adduser -S adent -G adent
COPY --from=build /app/target/adent-backend-*.jar app.jar
USER adent
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
