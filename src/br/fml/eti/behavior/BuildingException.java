package br.fml.eti.behavior;

/**
 * When something went wrong in {@link Factory#buildANewInstance()}.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:38:34 AM
 */
public class BuildingException extends Exception {
    public BuildingException() {
        super();
    }

    public BuildingException(String message) {
        super(message);
    }

    public BuildingException(Throwable cause) {
        super(cause);
    }

    public BuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
