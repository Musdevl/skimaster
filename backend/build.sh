#!/usr/bin/env bash

echo "Compiling the SKIMASTER Spring BACKEND within a multi-stage docker build"

docker build --build-arg JAR_FILE=SKIMASTER-0.0.1-SNAPSHOT.jar -t pcollet/tcf-spring-backend .
