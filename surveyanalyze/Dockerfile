FROM python:3.7

# Install Python and required dependencies
RUN apt-get update && apt-get install -y python3 python3-pip

# Set Python in PATH environment variable
ENV PATH="/usr/bin/python3:${PATH}"

# Copy the Spring application JAR file
COPY ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar

# Set the working directory
WORKDIR /usr/src/app

# Expose ports
EXPOSE 8080
EXPOSE 8082

# Run the Spring application
CMD ["java", "-jar", "-Dspring.profiles.active=server", "./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar"]
