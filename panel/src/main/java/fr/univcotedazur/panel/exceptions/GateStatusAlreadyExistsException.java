package fr.univcotedazur.panel.exceptions;

public class GateStatusAlreadyExistsException extends RuntimeException {
    public GateStatusAlreadyExistsException(String message) {
        super(message);
    }
}
