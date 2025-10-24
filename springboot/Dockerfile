FROM amazoncorretto:21.0.7

EXPOSE 8181

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]