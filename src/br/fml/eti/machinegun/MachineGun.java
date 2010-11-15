package br.fml.eti.machinegun;

/**
 * <p>
 * MachineGun avoid stopping the main flow when expensive things is needed,
 * like I/O operations. First, MachineGun will put everything in an (limited)
 * internal buffer, after some consumers will put it in an internal persisted
 * queue to finally, others consumers do the expensive operation.
 * </p>
 * <p>
 * Everything can be externally monitored, specially the operations times
 * mensured in nano seconds.
 * </p>
 *
 * @see Army
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 5:33:07 AM
 */
public abstract class MachineGun<T> {
    /**
     * You can use a {@link MachineGun} to "fire" the <code>data</code>
     * as fast as possible into an internal buffer to, after, be placed
     * in an internal queue to be processed as soon as possible until finally
     * reach the target.
     *
     * @param bullet The <b>data</b> to be processed.
     * @param capsule A {@link Capsule} is a way to keep the
     *                "bullet" (data) intact through the way to
     *                the target.
     * @param targetName Where and how the "bullet" (data) will reach the target.
     *               You <u>have to</u> register a target in the {@link Army}:
     *               see the {@link Army#registerTarget} to know how to do this.
     */
    public abstract void fire(T bullet, Capsule<T> capsule, String targetName) throws UnregisteredTargetException;
}
