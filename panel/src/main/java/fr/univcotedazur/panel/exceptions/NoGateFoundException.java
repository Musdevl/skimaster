package fr.univcotedazur.panel.exceptions;

public class NoGateFoundException extends RuntimeException {
    public NoGateFoundException(String gateName) {
        super("No gate found with name: " + gateName);
    }
}
