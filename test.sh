#!/usr/bin/env bash
#Remove any postgres database or ref-price app  containers/volumes
docker rm -f -v \
postgres-db \
ref-price-service

#Remove existing docker network
docker network rm ci-cd-network

#Create docker network
docker network create ci-cd-network

docker volume create --name gradle-cache

#Create a the ref-price docker database
docker run --network ci-cd-network \
--network-alias docker-psql \
--name postgres-db \
-e POSTGRES_PASSWORD=password \
-e POSTGRES_DB=ref-price \
-e POSTGRES_USER=root \
-d postgres:12.2

#Set the env as local and run the test
docker run  --network ci-cd-network \
--name ref-price-service \
--env  spring_profiles_active=local \
-v gradle-cache:/home/gradle/.gradle \
-v "$PWD":/Project -w /Project \
--rm \
gradle:6.3-jdk11 gradle test

#Stop and remove the postgres database container
docker rm -f postgres-db
