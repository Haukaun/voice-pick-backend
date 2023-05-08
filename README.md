# Voice Pick Backend

This is a Spring Boot Java backend for a Swift app named Voice Pick. The purpose of this project is to provide a REST API for the Swift app to interact with the backend.

## Getting Started

These instructions will help you get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17 or later
- Maven 3.5 or later
- Git
- Docker

### Installing

1. Clone the repository using the following command:
git clone https://gitlab.com/IDATA-2900-Group-1/voice-pick-backend.git

2. Change into the directory:
```
cd voice-pick-backend
```

3. Setup keycloak instance

- Create a docker container with the following command:
```
docker run --name <CONTAINER_NAME> -p 8443:8443 -d -e KEYCLOAK_USER=<USERNAME> -e KEYCLOAK_PASSWORD=<PASSWORD> jboss/keycloak
```
- Go to `http://localhost:8443` and log into admin console
- Import the keycloak config stored in the repo

4. Setup database

- Create a docker container with the following command:
```
docker run --name <CONTAINER_NAME> -e POSTGRES_PASSWORD=<PASSWORD> -d postgres
```

5. Add .env file with the values shown in the `.env.example` file:

- DB_HOST: url to a postgresql database
- DB_PORT: port of the hosted database
- DB_NAME: name of the schema to connect to
- DB_USER: username of the user 
- DB_PASSWORD: password of the user

- KEYCLOAK_BASE_URL: base url of the hosted keycloak instance. (If the docker approach from above was used, this should be `http://localhost:8443`)
- KEYCLOAK_REAL: name of the realm to connect to. With the config in the repo this can be `voice-pick-dev` or `voice-pick-prod`.
- KEYCLOAK_CLIENT_ID: id of the client to connect to. With the config in the repo this is `api`. Can be found and change under `clients` in the admin console.
- KEYCLOAK_CLIENT_SECRET: secret of the client to use. Found in `admin console > clients > (the client used) > credentials > secret`

- SMTP_USER: username of a SMTP server, we have used the SMTP service provided by apple
- SMTP_PASSWORD: password of a SMTP server

6. Build the project with Maven:
mvn clean install

7. Run the application:
mvn spring-boot:run


## API Documentation

The API documentation can be found in our swagger instance, `https://api.bachelor.seq.re/swagger-ui/index.html#/`. It provides detailed information about the endpoints and how to interact with them.

## Deployment

Can be deployed with:
```bash
docker build -t vp-backend .
```

```bash
docker run -d -p 8080:8080 --restart unless-stopped --name voice-pick-api vp-backend
```

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Maven](https://maven.apache.org/) - Dependency Management
