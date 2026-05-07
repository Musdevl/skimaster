package fr.univcotedazur.skimaster.monitoring.exceptions;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }
}
