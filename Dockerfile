FROM maven:3.6.0-jdk-17

WORKDIR /app
COPY ./src /app/src
COPY ./pom.xml /app

RUN mvn clean
RUN mvn package


WORKDIR /app/target/
EXPOSE 8080

RUN mv voice-pick* voice-pick.jar
CMD ["java", "-jar", "voice-pick.jar"]
