/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun;

/**
 * <p>
 * A {@link Capsule} is a way to keep the
 * "bullet" (data) intact through the way to
 * the {@link Target target}.
 * </p>
 * <p>
 * In a less abstract, an instance of this class is able to convert
 * the data <code>T</code> to an array of bytes and an array of bytes to
 * a <code>T</code> type again.
 * </p>
 * <small><small><small>
 * <pre>
                               ...
     c=   c=   c=    c=     ====||]
                                \\
                                                               
</pre></small></small></small>
 *
 * @see br.eti.fml.machinegun.util.GenericCapsuleForLazyPeople
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:51:35 AM
 */
public interface Capsule<T> {
    T restoreFromBytes(byte[] data) throws WrongCapsuleException;
    byte[] convertToBytes(T data) throws WrongCapsuleException;
}
