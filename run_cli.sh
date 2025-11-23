#!/bin/bash

# Ensure backend is running (optional check)
# echo "Checking if backend is running..."

# Export JAVA_HOME to ensure correct Java version
export JAVA_HOME=/Users/paulmoreno/.sdkman/candidates/java/17.0.9-tem

# Build and Run CLI
echo "Building CLI..."
cd cli
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Starting CLI..."
    # Run the shaded jar
    $JAVA_HOME/bin/java -jar target/vetclinic-cli-1.0-SNAPSHOT.jar
else
    echo "Build failed."
fi
