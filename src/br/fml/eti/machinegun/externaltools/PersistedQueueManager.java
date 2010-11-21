/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun.externaltools;

import br.fml.eti.machinegun.auditorship.ArmyAudit;

/**
 * <p>
 * It is recommended to implement this interface using
 * a JMS implementation, like the HornetQ, e.g.
 * </p>
 * <pre>

                       |
                   \  |  /
                    \_|_/
                  .'     '.
                 / _    _  \
                | / `  / `  |
                | \_0  \_0  |
            _  _ \  .---.  /
      ___/\/ \/ \_'/     \'____________
      \  \/\_/\_/  \     /            /
       )            '---'            (
      /           YOUR QUEUE          \
      \        IMPLEMENTATION         /
       )            HERE!      _  _  (
      /jgs__________________/\/ \/ \__\
                  |   |   | \/\_/\_/
                  |_  |  _|
                  |   |   |
                  |___|___|
                  /--'Y'--\
                 (___/ \___)
   </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:08:50 PM
 */
public interface PersistedQueueManager {
    /**
     * Put a data into a queue. Options of retries, logs and exceptions handling
     * must be done externally by the specific implementation.
     * This function assumes the action will
     * never crash: because it, depending of implementation, this function
     * could do nothing. If you want to take more control over it,
     * you are able to use
     * the {@link br.fml.eti.machinegun.auditorship.ArmyAudit} to intercept
     * some error; but it will work fine <u>only if</u> the specific
     * implementation gossips what its does to the
     * {@link br.fml.eti.machinegun.auditorship.ArmyAudit auditor}.
     * <b>Please read carefully the specific implementation documentation!</b>
     *
     * @throws InterruptedException Because it can be asynchronous, it may throw this.
     * @param armyAudit If something went wrong, you can know monitoring with this.
     * @param queueName The embedded queue name related with the specific type
     *                  of data.
     * @param data The data to be put in the queue.
     * @see br.fml.eti.machinegun.auditorship.ArmyAudit#errorWhenPuttingIntoAnEmbeddedQueue
     */
    void putIntoAnEmbeddedQueue(ArmyAudit armyAudit, String queueName, byte[] data)
            throws InterruptedException;

    /**
     * Prepare a new consumer to work on the new elements in the specified queue.
     * Options of retries, logs and exceptions handling
     * must be done externally by the specific implementation. This
     * function assumes the action will
     * never crash: because it, depending of implementation, this function
     * could do nothing. If you want take more control over it, you are able to use
     * the {@link br.fml.eti.machinegun.auditorship.ArmyAudit} to intercept
     * some error; but it will work fine <u>only if</u> the specific
     * implementation gossips what its does to the
     * {@link br.fml.eti.machinegun.auditorship.ArmyAudit auditor}.
     * <b>Please read carefully the specific implementation documentation!</b>
     *
     * @param armyAudit If something went wrong you can know monitoring with this.
     * @param queueName The embedded queue name related with the specific type
     *                  of data.
     * @param consumer The action to do when some element is available to consume.
     * @see br.fml.eti.machinegun.auditorship.ArmyAudit#errorWhenRegisteringANewConsumerInAnEmbeddedQueue
     */
    void registerANewConsumerInAnEmbeddedQueue(
            ArmyAudit armyAudit, String queueName, Consumer consumer);

    /**
     * Kill all consumers of a specific queue.
     *
     * @param queueName The queue name.
     * @throws InterruptedException Because it may be asynchronous.
     */
    void killAllConsumers(String queueName) throws InterruptedException;
}
