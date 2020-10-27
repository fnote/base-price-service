#pull base image
FROM openjdk:11

# Add build
COPY build/libs/ref-price-service-*.jar /app/

#Expose port
EXPOSE 8081

CMD java -jar app/ref-price-service-*.jar
