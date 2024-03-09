FROM openjdk:21-slim

WORKDIR /opt

EXPOSE 8080/tcp

COPY target/*.jar /opt/snapshotter/lib/snapshotter.jar

USER spring

ENTRYPOINT ["java", "-jar", "cardano-shield-api/snapshotter.jar"]