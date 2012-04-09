/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun.auditorship;

import br.eti.fml.behavior.BuildingException;
import br.eti.fml.machinegun.WrongCapsuleException;

/**
 * <p>
 * Almost everything that happens in {@link br.eti.fml.machinegun.Army}
 * is reported here, don't worry. Irrelevant things will not be reported due
 * these things are internal implementations with constant time, as the
 * internal buffer consumers, e.g.
 * </p>
 * <p>
 * It is useful if you need to log and/or measure the time of the main operations.
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
     * {@link br.eti.fml.machinegun.DirtyWork dirty work}.
     *
     * @param consumerName A consumer name like "Consumer 2 of 8".
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between start and end events.
     */
    void aConsumerStartsHisJob(long jobId, String consumerName);

    /**
     * Soon after a consumer finishes his
     * {@link br.eti.fml.machinegun.DirtyWork job}.
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
     * If <code>newSize / maxSize</code> is near to 100%
     * the internal buffer is overloaded and a call to
     * {@link br.eti.fml.machinegun.MachineGun#fire} can block
     * for a while.
     *
     * @param newSize Elements into an internal buffer to be consumed.
     * @param maxSize Maximum capacity of the internal buffer.
     */
    void updateCurrentBufferSize(int newSize, int maxSize);

    /**
     * When a consumer of the persisted embedded queue is ready to consume.
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
     * @see br.eti.fml.machinegun.Capsule
     * @see br.eti.fml.machinegun.WrongCapsuleException
     * @param e Exception
     */
    void errorOnDataSerialization(WrongCapsuleException e);

    /**
     * It is called if something went wrong on
     * {@link br.eti.fml.machinegun.externaltools.PersistedQueueManager#putIntoAnEmbeddedQueue}.
     * @param e The root of the problem
     */
    void errorWhenPuttingIntoAnEmbeddedQueue(Exception e);

    /**
     * It ss called if something went wrong on
     * {@link br.eti.fml.machinegun.externaltools.PersistedQueueManager#registerANewConsumerInAnEmbeddedQueue}.
     * @param e The cause
     */
    void errorWhenRegisteringANewConsumerInAnEmbeddedQueue(Exception e);
}
