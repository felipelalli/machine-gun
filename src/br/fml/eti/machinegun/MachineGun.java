package br.fml.eti.machinegun;

/**
 * <p>
 * A "machine gun" avoid stopping the main flow when expensive things is needed,
 * like I/O operations. First, {@link MachineGun} will put everything in an (limited)
 * internal buffer, after some consumers will put it in an internal persisted
 * queue to finally, others consumers do the expensive operation. A "machine gun"
 * is very useful, e.g., when you need a fast (and asynchronous) persistence into
 * a (slow) database. 
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
     * @throws UnregisteredMissionException If you forget to start a mission
     *                                      before using {@link Army#startNewMission}.
     * @throws InterruptedException If the army is busy (internal buffer is full)
     *                              so this function can block indefinitely.
     *
     * @param bullet The <b>data</b> to be processed.
     *
     * @param missionName Where and how the "bullet" (data) will reach the target.
     *               You <u>have to</u> register a mission in the {@link Army}:
     *               see the {@link Army#startNewMission} to know how to do this.
     */
    public abstract void fire(T bullet, String missionName)
            throws UnregisteredMissionException, InterruptedException;
}
