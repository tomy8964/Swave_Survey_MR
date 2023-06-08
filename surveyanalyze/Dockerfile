FROM openjdk:17

# Install Python and required dependencies
RUN apt-get update && apt-get install -y python3 python3-pip

# Set Python in PATH environment variable
ENV PATH="/usr/bin/python3:${PATH}"

WORKDIR /usr/src/app

COPY ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar

EXPOSE 8080
EXPOSE 8082

# Add Python scripts or files
COPY python-scripts/ /usr/src/app/python-scripts/

# Set Python environment variables
ENV PYTHONPATH=/usr/src/app/python-scripts
ENV PYTHONUNBUFFERED=1

# Set the working directory
WORKDIR /usr/src/app

# Run the Spring application with Python support
CMD ["java", "-jar", "-Dspring.profiles.active=server", "./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar"]

#local
#CMD ["java","-jar","./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar"]
