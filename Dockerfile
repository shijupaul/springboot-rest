FROM java:openjdk-8
RUN mkdir app
ADD target/rest-exception-handling-0.0.1-SNAPSHOT.jar app/
ENTRYPOINT java -jar app/rest-exception-handling-0.0.1-SNAPSHOT.jar