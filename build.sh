#!/usr/bin/env bash
#Create docker volume for caching
docker volume create --name gradle-cache

docker run --rm \
-v gradle-cache:/home/gradle/.gradle \
-v "$PWD":/Project -w /Project \
gradle:6.3-jdk11 gradle clean assemble
