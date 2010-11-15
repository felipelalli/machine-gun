package br.fml.eti.machinegun.test.mocks;

import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.Consumer;
import br.fml.eti.machinegun.externaltools.QueueManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:19:53 PM
 */
public class FakeQueueManager implements QueueManager {
    private Map<String, BlockingQueue<byte[]>> queues
            = new HashMap<String,  BlockingQueue<byte[]>>();

    private Random random = new Random();
    private ArrayList<Thread> threads = new ArrayList<Thread>();

    @Override
    public void putInAEmbeddedQueue(ArmyAudit armyAudit,
                                    String queueName, byte[] data)
            throws InterruptedException {

        if (!queues.containsKey(queueName)) {
            queues.put(queueName, new LinkedBlockingQueue<byte[]>());
        }

        long timeToWait = random.nextInt(500);
        Thread.sleep(timeToWait);

        queues.get(queueName).put(data);
    }

    int size = 0;

    @Override
    public void registerAConsumerInEmbeddedQueue(final ArmyAudit armyAudit,
                                                 final String queueName,
                                                 final Consumer consumer) {

        size++;

        Thread t = new Thread("mock consumer " + size + " of " + size) {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        byte[] data = queues.get(queueName).take();
                        consumer.consume(data);
                    } catch (InterruptedException e) {
                        armyAudit.rearSoldierDied(Thread.currentThread().getName());
                    }
                }
            }
        };

        threads.add(t);
        t.start();
    }

    @Override
    public void killAllConsumers(String queueName) throws InterruptedException {
        for (Thread t : threads) {
            t.interrupt();
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}
