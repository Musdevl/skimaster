package fr.univcotedazur.skimaster.monitoring.exceptions;

public class FailedToGetGateStatusException extends RuntimeException {
    public FailedToGetGateStatusException(String message) {
        super(message);
    }
}
