FROM openjdk:21-slim

WORKDIR /opt

EXPOSE 8080/tcp

COPY target/infinity-pools-snapshotter.jar ./infinity-pools-snapshotter.jar

ENTRYPOINT ["java", "-jar", "infinity-pools-snapshotter.jar"]