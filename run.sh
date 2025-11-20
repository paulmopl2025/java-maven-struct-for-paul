#!/bin/sh
docker compose up -d
export JAVA_HOME=/Users/paulmoreno/.sdkman/candidates/java/17.0.9-tem && mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments="--server.port=8081"