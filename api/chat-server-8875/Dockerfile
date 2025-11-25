FROM eclipse-temurin:17-jdk
EXPOSE 8080
MAINTAINER cherry

VOLUME /mydata
COPY /chat-server-8875.jar /chatserver.jar
ENTRYPOINT ["java","-jar","/chatserver.jar","--spring.profiles.active=prod"]
