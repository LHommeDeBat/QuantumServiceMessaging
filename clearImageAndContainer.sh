echo "Build QuantumServiceFilesystem"
mvn clean install -DskipTests
echo "Removing quantum service container..."
docker container rm -f quantumservicefilesystem
echo "Removing quantum service image..."
docker image rm -f quantumservicefilesystem

echo "Removing db container..."
docker container rm -f quantumservicefilesystemdb

echo "Removing mq container..."
docker container rm -f quantumservicefilesystemmq
