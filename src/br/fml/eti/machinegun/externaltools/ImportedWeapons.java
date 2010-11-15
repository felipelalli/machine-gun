package br.fml.eti.machinegun.externaltools;

/**
 * External tools as queues specific implementations.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:35:40 PM
 */
public class ImportedWeapons {
    private QueueManager queueManager;

    public ImportedWeapons(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }
}
