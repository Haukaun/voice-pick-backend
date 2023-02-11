FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY ./target/voice-pick*.jar /app
RUN mv voice-pick*.jar voice-pick.jar


EXPOSE 8080

CMD ["java", "-jar", "voice-pick.jar"]
