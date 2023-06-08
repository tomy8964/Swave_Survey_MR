FROM openjdk:17

# Install curl and other dependencies
RUN apt-get update && apt-get install -y curl gnupg

# Download and install Python
RUN curl https://www.python.org/ftp/python/3.9.7/Python-3.9.7.tgz --output python.tgz \
    && tar -xzf python.tgz \
    && cd Python-3.9.7 \
    && ./configure \
    && make install \
    && cd .. \
    && rm -rf python.tgz Python-3.9.7

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
