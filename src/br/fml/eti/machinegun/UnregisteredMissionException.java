/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun;

/**
 * See {@link MachineGun#fire}.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:52:04 AM
 */
public class UnregisteredMissionException extends RuntimeException {
    public UnregisteredMissionException(String message) {
        super(message);
    }
}
