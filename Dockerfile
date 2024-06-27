FROM busybox AS network-check
RUN ping -c 1 google.com

FROM adoptopenjdk:17-jre-hotspot
COPY target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
