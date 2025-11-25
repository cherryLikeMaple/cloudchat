FROM eclipse-temurin:17-jdk
EXPOSE 8066
MAINTAINER cherry

VOLUME /mydata
COPY /file-service-66-1.0-SNAPSHOT.jar /file-service.jar
ENTRYPOINT ["java","-jar","/file-service.jar","--spring.profiles.active=prod"]
