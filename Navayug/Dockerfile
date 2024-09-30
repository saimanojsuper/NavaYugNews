# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17-alpine-jdk

# Set the working directory in the container
WORKDIR /Navayug

ARG JAR_FILE=build/libs/Navayug-0.0.1-SNAPSHOT.jar

# Copy the packaged JAR file into the container
COPY ${JAR_FILE} /Navayug/Navayug.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/Navayug/Navayug.jar"]
