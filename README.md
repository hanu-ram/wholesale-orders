# wholesale-orders

spring-boot-gradle-starter
-----

This is a Spring Boot gradle starter template for multi module project.
What this starter template contains -

###common-module

    1. Checkstyle for code formatting
    2. Jacoco - for code coverage
    3. Spotbugs and PMD - for static code analysis
    4. Dockerfile -for creating docker image
    5. Actuator - for actuator end points
    6. swagger - for generating OAS specification - http://localhost:8080/swagger-ui.html
    7. Standard Error Response - for generating error responses in a standard format

### config

1. Configuration for checkstyle
2. configuration for findbug
3. configuration for pmd rules

## kafka-client

1. kafka dependencies and utility methods requires for kafka communication

## service-generator
     This module contains the template to generate microservice,kafka consumer, kafka producer , utility module skeleton.
     More detail can be find [here](../service-generator/README.md)

## generating the skeleton
     You can add the new module in the project by running following task
     ./gradlew :service-generator:generate -PmoduleType=microservice -PmoduleName=my-module

## Building the mcroservices modules

You can build the application `./gradlew :modulename:build`.
This will run all tasks including checkstyle, spotbugs, pmd and test cases.
Next, you can run the application by executing:

```bash
$ java -jar build/spring-boot-starter-0.0.1-SNAPSHOT.jar
```
or
```
./gradlew :modulename:bootrun 
```
The application will be accessible at `http://localhost:8080`.


## Features

This starter comes bundled with the following features:

1. Spring boot application with default API.
2. gradle wrapper: So, you don't need to install gradle on your machine.
3. Checkstyle: Enforce sane coding standard guidelines.
4. CORS enabled: A global configuration is added to enable CORS so that frontend can work seamlessly with backend during development.


## Docker Setup

You can build the docker image using the following gradle command
```
$ ./gradlew docker
```

Alternatively -

To build the docker images and start the containers using Docker Compose run the following command.
This will work in the *nix systems.

```
$ sh docker.sh
```

You can view running docker containers by executing following command.

```
$ docker ps
``` 

To stop and remove all docker container you have to run following command.
This command should be run from project root.

```
$ docker-compose stop && docker-compose rm --force
``` 