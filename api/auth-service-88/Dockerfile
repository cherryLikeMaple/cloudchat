FROM eclipse-temurin:17-jdk
EXPOSE 8088
MAINTAINER cherry

VOLUME /mydata
COPY /auth-service-88-1.0-SNAPSHOT.jar /auth-service.jar
ENTRYPOINT ["java","-jar","/auth-service.jar","--spring.profiles.active=prod"]
