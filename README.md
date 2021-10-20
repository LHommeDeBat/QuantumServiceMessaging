# QuantumService (FaaS-Alternative using Filesystem)

This alternative version of the [QuantumService](https://github.com/LHommeDeBat/QuantumServiceFaas) relies on a filesystem and the usage of the Python-CLI instead of using FaaS for deploying and executing python scripts written with qiskit.

When a quantum application is submitted, the service uses the submitted code of the application to write a file to disk.
For execution, it uses the Python-CLI from inside the service to execute the python files (the machine requires an additional python runtime with qiskit installed).

## How to use

Before starting the application, rename the **docker-compose-template.yml** to **docker-compose-yml** and replace missing variables marked with **!...!**. In this case, it is only the volume for the mysql-database.

To start the needed database and MQ-Server perform **docker-compose up -d** in root folder of the project. This should start the container **quantumservicefilesystemdb** at port 9025 and **quantumservicefilesystemmq** at port 1414

Then use your IDE like IntelliJ to start the spring boot application.
Please not that the application needs a python runtime with installed qiskit to run all registered quantum applications.
Make sure that your local machine where you run the application has **java** and **python + qiskit** installed.

To configure the application to your will, please check the necessary environment variables in **application.yml** and **application-local.yml**.
All of the variables have default values besides the **apiToken: ${IBMQ_API_TOKEN}** within the **application.yml**. This variable needs to be passed to the application or simply overwritten by the actual value of your IBM Quantum API-Token.

To use the service after it has started the swagger-ui can be used by opening:

{{QUANTUM_SERVICE_MSG_HOST}}/swagger-ui/

(example: http://localhost:9005/swagger-ui/)

in the browser.

Alternatively a Frontend-UI called [QuanatumServiceUI](https://github.com/LHommeDeBat/QuantumServiceUI) can be used.

## Supported Feeds

- QueueSizeEvent-Feed: Retrieves queue sizes of all available quantum computers that are provided by IBM Quantum. Only input parameters submitted by the events are the **device** and **apiToken**. The two parameters do not have to be provided during the creation of a quantum application, they will be automatically used for invoking the registered quantum applications.
- ExecutionResultEvent-Feed: Retrieves results of completed quantum application executions. Events from this feed provide **device**, **apiToken** and **result**. The **result** parameter will be passed as a JSON-String and it needs to be provided during quantum application creation (with a default value) if the said quantum application wants to register to a ExecutionResultEvent-Feed.

## Creation of Quantum Applications

During the creation of quantum applications a Multipart-POST-Request is sent to:

{{QUANTUM_SERVICE_MSG_HOST}}/quantum-applications

(example: http//localhost:9005/quantum-applications)

with parts:

- script: Your python file containing the qiskit code that is written as a function (function must be called **main**, it only has to have one single input parameter and the return must contain a single object with the field **jobId** that returns the Job-ID of the created Job)
- dto: Other metadata of the quantum application formatted as a json string

dto example:

``` json
{
   "name":"TestResultJson",
   "parameters":{
      "result":{
         "type":"STRING",
         "defaultValue":"{}"
      }
   }
}
```

script example:

``` python
from qiskit import IBMQ, transpile
from qiskit.circuit.random import random_circuit

def main(params):
    provider = IBMQ.enable_account(params['apiToken'])
    backend = provider.get_backend(params['device'])

    qx = random_circuit(num_qubits=5, depth=4)
    transpiled = transpile(qx, backend=backend)
    job = backend.run(transpiled)
    
    return {
        "jobId": job.job_id()
    }
```

The creation of the quantum application will not only create a database entry but also store the python files to disk inside a **actions**-folder, from where they can be executed using the Python-CLI.

## Creation of Event-Triggers

To register quantum applications to specific feeds, so called **EventTriggers** need to be created by sending POST-Requests to:

{{QUANTUM_SERVICE_MSG_HOST}}/event-triggers

(example: http//localhost:9005/event-triggers)

with request-body:

``` json
{
   "name":"TestQueueSizeTrigger",
   "eventType":"QUEUE_SIZE",
   "queueSize":55555
}
```

as an exaple for a QueueSizeEventTrigger or:

``` json
{
   "name":"TestExecutionResultEventTrigger",
   "eventType":"EXECUTION_RESULT",
   "executedApplication":{
      "id":"1c4c818b-1815-4212-afa5-508aa1dd56d8",
      "name":"TestResult",
      ...
      }
   }
}
```
as an example for a ExecutionResultEventTrigger

## Register QuantumApplications to Events

To register a quantum application to an event, it needs to be linked with a event trigger.
For that a POST-Request needs to be sent to:

{{QUANTUM_SERVICE_MSG_HOST}}/event-triggers/{{TRIGGER_NAME}}/quantum-applications/{{QUANTUM_APPLICATION_NAME}}

(example: http//localhost:9005/event-triggers/MyTrigger/quantum-applications/MyQuantumApplication)

To deregister a quantum application from a event-feed, simply perform a DELETE-Request to the same URL.
