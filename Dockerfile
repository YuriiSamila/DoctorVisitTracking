FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/doctorvisittracking-0.0.1-SNAPSHOT.jar doctorvisittracking.jar
ENTRYPOINT ["java", "-jar", "doctorvisittracking.jar"]
EXPOSE 8080