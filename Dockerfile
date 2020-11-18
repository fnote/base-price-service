#pull base image
FROM 037295147636.dkr.ecr.us-east-1.amazonaws.com/dockerhub/openjdk:11

# Add build
COPY build/libs/ref-price-service-*.jar /app/

# Download Datadog tracer agent
RUN wget -O dd-java-agent.jar https://dtdg.co/latest-java-tracer

#Expose port
EXPOSE 8081

CMD java -javaagent:/dd-java-agent.jar -jar app/ref-price-service-*.jar
