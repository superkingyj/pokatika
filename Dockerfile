FROM adoptopenjdk/openjdk15
ARG JAR_FILE_PATH=build/libs/*.jar
EXPOSE 8080
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]