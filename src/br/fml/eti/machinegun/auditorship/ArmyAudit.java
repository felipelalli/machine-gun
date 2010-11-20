package br.fml.eti.machinegun.auditorship;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.WrongCapsuleException;

/**
 * <p>
 * Almost everything that happens in {@link br.fml.eti.machinegun.Army}
 * is reported here, don't worry. Irrelevant things will not be reported due
 * these things are internal implementations with constant time, as the
 * internal buffer consumers, e.g.
 * </p>
 * <pre>
 *               ,
     __  _.-"` `'-.
    /||\'._ __{}_(
    ||||  |'--.__\
    |  L.(   ^_\^
    \ .-' |   _ |
    | |   )\___/
    |  \-'`:._]
jgs \__/;      '-.
 </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:17:13 PM
 */
public interface ArmyAudit {
    /**
     * When a consumer found an element
     * to consume and starts to execute the
     * {@link br.fml.eti.machinegun.DirtyWork dirty work}.
     *
     * @param consumerName A consumer name like "Consumer 2 of 8".
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between start and end events.
     */
    void aConsumerStartsHisJob(long jobId, String consumerName);

    /**
     * Soon after a consumer finishes his
     * {@link br.fml.eti.machinegun.DirtyWork job}.
     *
     * @param consumerName A consumer name like "Consumer 2 of 8".
     * @param success <code>true</code> if everything was OK
     * @param exception if <code>success</code> is <code>false</<code>, this exception
     *          can be different of <code>null</code>. It will be <code>null</code>
     *          if <code>success</code> is <code>true</code>.
     * @param resultDetails A message giving details of status operation.
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between the <i>start</i> and <i>end</i>. 
     */
    void aConsumerHasBeenFinishedHisJob(long jobId, String consumerName,
                                   boolean success, BuildingException exception,
                                   String resultDetails);

    /**
     * If <code>newSize / maxSize</code> is near to 100%, it means
     * that the internal buffer is overloaded and a call to
     * {@link br.fml.eti.machinegun.MachineGun#fire} can block
     * for a while.
     *
     * @param newSize Elements into an internal buffer to be consumed.
     * @param maxSize Maximum capacity of the internal buffer.
     */
    void updatePreBufferCurrentSize(int newSize, int maxSize);

    /**
     * When a consumer of embedded queue is ready to consume.
     * @param consumerName A consumer name like "Consumer 3 of 7".
     */
    void consumerIsReady(String consumerName);

    /**
     * It happens when a mission has been finished. All consumers
     * will die.
     * 
     * @param consumerName A consumer name like "Consumer 3 of 7".
     */
    void consumerHasBeenStopped(String consumerName);

    /**
     * Happens when something went wrong in a data conversion (serialization
     * or deserialization).
     *
     * @see br.fml.eti.machinegun.Capsule
     * @see br.fml.eti.machinegun.WrongCapsuleException
     * @param e Exception
     */
    void errorOnDataSerialization(WrongCapsuleException e);

    /**
     * Is called if something went wrong on
     * {@link br.fml.eti.machinegun.externaltools.PersistedQueueManager#putIntoAnEmbeddedQueue}.
     * @param e The cause
     */
    void errorWhenPuttingIntoAnEmbeddedQueue(Exception e);

    /**
     * Is called if something went wrong on
     * {@link br.fml.eti.machinegun.externaltools.PersistedQueueManager#putIntoAnEmbeddedQueue}.
     * @param e The cause
     */    
    void errorWhenRegisteringANewConsumerInAnEmbeddedQueue(Exception e);
}
