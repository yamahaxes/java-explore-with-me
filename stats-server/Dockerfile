FROM amazoncorretto:11
WORKDIR /app
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java","-jar","stats-server.jar"]