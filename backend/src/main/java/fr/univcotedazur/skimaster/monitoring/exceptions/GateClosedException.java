package fr.univcotedazur.skimaster.monitoring.exceptions;

public class GateClosedException extends RuntimeException {
    public GateClosedException(String gateId) {
        super("Gate is closed: " + gateId);
    }
}
