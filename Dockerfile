#pull base image
FROM openjdk:11

RUN wget https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2019.8-docker.zip -P /tmp/ && \
  unzip /tmp/YourKit-JavaProfiler-2019.8-docker.zip -d /usr/local && \
  rm /tmp/YourKit-JavaProfiler-2019.8-docker.zip


ENV LD_LIBRARY_PATH=/lib64

EXPOSE 10001

# Add build
COPY build/libs/ref-price-service-0.0.1-SNAPSHOT.jar /app/

#Expose port
EXPOSE 8081

ENTRYPOINT ["java","-agentpath:/usr/local/YourKit-JavaProfiler-2019.8/bin/linux-x86-64/libyjpagent.so=port=10001,listen=all","-jar","app/ref-price-service-0.0.1-SNAPSHOT.jar"]
