# QuantumService (FaaS-Alternative using Filesystem)

This alternative version of the [QuantumService](https://github.com/LHommeDeBat/QuantumServiceFaas) relies on a filesystem and the usage of the Python-CLI instead of using FaaS for deploying and executing python scripts written with qiskit.

When a quantum application is submitted, the service uses the submitted code of the application to write a file to disk.
For execution, it uses the CLI outside of the Java-Service to execute the python files (the machine requires an additional python runtime with qiskit installed).
