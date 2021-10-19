# QuantumService (FaaS-Alternative using Filesystem)

This alternative version of the [QuantumService](https://github.com/LHommeDeBat/QuantumServiceFaas) relies on a filesystem and the usage of the Python-CLI instead of using FaaS for deploying and executing python scripts written with qiskit.

When a quantum application is submitted, the service uses the submitted code of the application to write a file to disk.
For execution, it uses the CLI outside of the running service to execute the python files (the machine requires an additional python runtime with qiskit installed).

## How to use

To start the needed database and MQ-Server perform **docker-compose up -d** in root folder of the project. This should start the container **quantumservicefilesystemdb** at port 9025 and **quantumservicefilesystemmq** at port 1414

Then either use your IDE like IntelliJ to start the spring boot application or build it with **mvn clean install -DskipTests** in your root folder.
Then go to your target folder and run the app by typing **java -jar QuantumService-0.0.1-SNAPSHOT.jar**
Please not that the application needs a python runtime with installed qiskit to run all registered quantum applications.
Make sure that your local machine where you run the application has **java** and **python + qiskit** installed.

To configure the application to your will, please check the necessary environment variables in **application.yml** and **application-local.yml**.
All of the variables have default values besides the **apiToken: ${IBMQ_API_TOKEN}** within the **application.yml**. This variable needs to be passed to the application or simply overwritten by the actual value of your IBM Quantum API-Token.
