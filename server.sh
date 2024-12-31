#!/bin/bash

kill -9 $(lsof -t -i:8080)

EXISTING_CONTAINER=$(sudo docker ps -aq -f name="web-app")

if [ -n "$EXISTING_CONTAINER" ]; then
    echo "Container with name $CONTAINER_NAME exists. Stopping and removing it..."
    sudo docker stop $CONTAINER_NAME
    sudo docker rm $CONTAINER_NAME
    echo "Container $CONTAINER_NAME removed."
else
    echo "No container with the name $CONTAINER_NAME exists."
fi

git clone https://github.com/jungle-royale/jungle-royale-web-server.git

sudo docker build -t web-app .
echo "Docker image built successfully."


# Run the Docker container
echo "Running the Docker container on port $PORT..."

sudo docker rm -f web-app 2>/dev/null || true
sudo docker run -d -p 8080:8080 --name web-app web-app

if [ $? -ne 0 ]; then
    echo "Docker container failed to start! Exiting..."
    exit 1
fi
echo "Docker container is running and mapped to port $PORT."

rm -rf jungle-royale-web-server
