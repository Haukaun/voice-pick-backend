package no.ntnu.bachelor.voicepick.exceptions;

/**
 * An exception that can be thrown when a object if not of the expected entity
 *
 * @author Joakim
 */
public class IllegalEntityException extends RuntimeException {

    public IllegalEntityException(String msg) { super(msg); }

}
