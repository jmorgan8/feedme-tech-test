FROM adoptopenjdk/openjdk14:ubi
COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar /usr/src/
CMD [ "java", "-jar", "/usr/src/demo-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
