package br.fml.eti.machinegun;

/**
 * See {@link MachineGun#fire}.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:52:04 AM
 */
public class UnregisteredTargetException extends Exception {
    public UnregisteredTargetException() {
        super();
    }

    public UnregisteredTargetException(String message) {
        super(message);
    }

    public UnregisteredTargetException(Throwable cause) {
        super(cause);
    }

    public UnregisteredTargetException(String message, Throwable cause) {
        super(message, cause);
    }
}
