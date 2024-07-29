FROM openjdk:11-jre-slim-buster
VOLUME /tmp
ARG JAR_FILE
COPY build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java","-Dfile.encoding=UTF-8","-Xms1024m","-Xmx2048m", "-jar","app.jar"]
