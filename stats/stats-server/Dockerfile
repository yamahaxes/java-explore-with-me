FROM amazoncorretto:17
WORKDIR /app
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java","-jar","stats-server.jar"]