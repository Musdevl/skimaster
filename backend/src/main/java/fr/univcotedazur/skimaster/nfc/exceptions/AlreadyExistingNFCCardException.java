package fr.univcotedazur.skimaster.nfc.exceptions;

public class AlreadyExistingNFCCardException extends RuntimeException {
    public AlreadyExistingNFCCardException(String message) {
        super(message);
    }
}