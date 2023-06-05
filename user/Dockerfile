FROM openjdk:11-jdk

WORKDIR /usr/src/app

COPY ./build/libs/user-0.0.1-SNAPSHOT.jar ./build/libs/user-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java","-jar","./build/libs/user-0.0.1-SNAPSHOT.jar"]