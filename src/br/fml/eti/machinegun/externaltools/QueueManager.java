package br.fml.eti.machinegun.externaltools;

import br.fml.eti.machinegun.auditorship.ArmyAudit;

/**
 * It is recommended to implement it using a JMS implementation, as
 * HornetQ for example.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:08:50 PM
 */
public interface QueueManager {
    /**
     * Put data into a queue. Retry options, logs and exceptions handling
     * must be done externally by the specific implementation because it
     * does not throw any exception. This function assumes the action will
     * never crash: because it, depending of implementation, this function
     * could do nothing. If you want take more control, you can use
     * the {@link br.fml.eti.machinegun.auditorship.ArmyAudit}; but
     * it will work fine only if the specific implementation tell what it does
     * to the auditor. See the specific implementation documentation.
     *
     * @param armyAudit If something got wrong you can know monitoring with this.
     * @param queueName The embedded queue name related with the specific type
     *                  of data.
     * @param data The data to be put in the queue.
     */
    void putInAEmbeddedQueue(ArmyAudit armyAudit, String queueName, byte[] data);

    /**
     * Prepare a new consumer to work on new elements in the specified queue.
     * Retry options, logs and exceptions handling
     * must be done externally by the specific implementation because it
     * does not throw any exception. This function assumes the action will
     * never crash: because it, depending of implementation, this function
     * could do nothing. If you want take more control, you can use
     * the {@link br.fml.eti.machinegun.auditorship.ArmyAudit}; but
     * it will work fine only if the specific implementation tell what it does
     * to the auditor. See the specific implementation documentation.
     *
     * @param armyAudit If something got wrong you can know monitoring with this.
     * @param queueName The embedded queue name related with the specific type
     *                  of data.
     * @param consumer The action to do when some element is available to consume.
     */
    void registerAConsumerInEmbeddedQueue(ArmyAudit armyAudit,
                                          String queueName, Consumer consumer);
}
