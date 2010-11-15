package br.fml.eti.machinegun;

/**
 * When it was not possible to convert a byte array into the original
 * data or vice-versa.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:30:47 PM
 */
public class WrongCapsuleException extends Exception {
    public WrongCapsuleException(String message) {
        super(message);
    }

    public WrongCapsuleException(String message, Throwable t) {
        super(message, t);
    }

    public WrongCapsuleException(Throwable t) {
        super(t);
    }

    public WrongCapsuleException() {
        super();
    }
}
