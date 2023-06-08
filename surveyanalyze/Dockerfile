FROM python:3.7

# Set working directory
WORKDIR /usr/src/app

# Install Java runtime
RUN apt-get update && apt-get install -y default-jre

# Copy the Spring application JAR file
COPY ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar ./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar

# Expose ports
EXPOSE 8080
EXPOSE 8082

# Add Python scripts or files
COPY python-scripts/ /usr/src/app/python-scripts/

# Set Python environment variables
ENV PYTHONPATH=/usr/src/app/python-scripts
ENV PYTHONUNBUFFERED=1

# Run the Spring application with Python support
CMD ["java", "-jar", "-Dspring.profiles.active=server", "./build/libs/surveyanalyze-0.0.1-SNAPSHOT.jar"]
