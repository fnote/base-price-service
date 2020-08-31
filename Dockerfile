#pull base image
FROM openjdk:11

# Add build
COPY build/libs/ref-price-service-0.0.1-SNAPSHOT.jar /app/

#Expose port
EXPOSE 8081

ENTRYPOINT ["java","-jar", "app/ref-price-service-0.0.1-SNAPSHOT.jar"]
