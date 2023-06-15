FROM openjdk:11-jdk
COPY ./plantodo-0.0.1-SNAPSHOT.jar app.jar
COPY ./keystore.p12 keystore.p12
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8443