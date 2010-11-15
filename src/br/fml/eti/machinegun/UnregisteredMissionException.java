package br.fml.eti.machinegun;

/**
 * See {@link MachineGun#fire}.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:52:04 AM
 */
public class UnregisteredMissionException extends Exception {
    public UnregisteredMissionException() {
        super();
    }

    public UnregisteredMissionException(String message) {
        super(message);
    }

    public UnregisteredMissionException(Throwable cause) {
        super(cause);
    }

    public UnregisteredMissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
