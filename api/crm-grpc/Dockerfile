FROM openjdk:17 AS dev
COPY . ./app
WORKDIR /app
RUN ./mvnw clean package
COPY ./target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar","./app.jar"]