#pull base image
FROM openjdk:11

# Add build
COPY build/libs/payplus-api-0.0.1-SNAPSHOT.jar /app/

#Expose port
EXPOSE 8081

ENTRYPOINT ["java","-jar", "app/payplus-api-0.0.1-SNAPSHOT.jar"]