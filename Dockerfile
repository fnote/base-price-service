#pull base image
FROM 037295147636.dkr.ecr.us-east-1.amazonaws.com/dockerhub/openjdk:11

# Add build
COPY build/libs/ref-price-service-*.jar /app/

#Expose port
EXPOSE 8081

CMD java -jar app/ref-price-service-*.jar
