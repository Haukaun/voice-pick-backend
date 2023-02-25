FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY ./voice-pick*.jar /app
RUN mv voice-pick*.jar voice-pick.jar

ENV DB_HOST=$DB_HOST
ENV DB_PORT=$DB_PORT
ENV DB_NAME=$DB_NAME
ENV DB_USER=$DB_USER
ENV DB_PASSWORD=$DB_PASSWORD

ENV KEYCLOAK_BASE_URL=$KEYCLOAK_BASE_URL
ENV KEYCLOAK_REALM=$KEYCLOAK_REALM
ENV KEYCLOAK_CLIENT_ID=$KEYCLOAK_CLIENT_ID
ENV KEYCLOAK_CLIENT_SECRET=$KEYCLOAK_CLIENT_SECRET
ENV KEYCLOAK_MANAGER_USERNAME=$KEYCLOAK_MANAGER_USERNAME
ENV KEYCLOAK_MANAGER_PASSWORD=$KEYCLOAK_MANAGER_PASSWORD


EXPOSE 8080

CMD ["java", "-jar", "voice-pick.jar"]
