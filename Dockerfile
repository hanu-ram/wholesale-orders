FROM openjdk:11-jre-slim-buster
VOLUME /tmp
RUN apt-get update && apt-get install -y curl unzip && \
    curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip && \
    unzip newrelic-java.zip && \
    rm newrelic-java.zip
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8080
CMD ["java","-javaagent:/newrelic/newrelic.jar","-Dnewrelic.config.api_host=rpm.newrelic.com","-Dfile.encoding=UTF-8","-Xms1024m","-Xmx2048m", "-jar","app.jar"]
