package de.unistuttgart.iaas.messaging.quantumservice.model.exception;

public class QuantumApplicationScriptException extends RuntimeException {

    public QuantumApplicationScriptException(String message) {
        super(message);
    }

    public QuantumApplicationScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
