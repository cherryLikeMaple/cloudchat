FROM eclipse-temurin:17-jdk
EXPOSE 8077
MAINTAINER cherry

VOLUME /mydata
COPY /main-service-77-1.0-SNAPSHOT.jar /main-service.jar
ENTRYPOINT ["java","-jar","/main-service.jar","--spring.profiles.active=prod"]
