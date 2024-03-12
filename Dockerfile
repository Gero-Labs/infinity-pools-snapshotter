FROM openjdk:21-slim

WORKDIR /opt

EXPOSE 8080/tcp

COPY target/snapshotter.jar ./snapshotter.jar

ENTRYPOINT ["java", "-jar", "snapshotter.jar"]