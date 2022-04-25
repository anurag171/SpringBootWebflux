FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
COPY ./target/movie-info-service.jar movie-info-service.jar
ENTRYPOINT ["java","-jar","/movie-info-service.jar"]