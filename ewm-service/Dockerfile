FROM amazoncorretto:17
WORKDIR /app
COPY target/*.jar ewm-service.jar
ENTRYPOINT ["java","-jar","ewm-service.jar"]