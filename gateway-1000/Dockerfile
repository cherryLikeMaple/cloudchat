FROM eclipse-temurin:17-jdk
EXPOSE 1000
MAINTAINER cherry

VOLUME /mydata
COPY /gateway-1000-1.0-SNAPSHOT.jar /gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar","--spring.profiles.active=prod"]
