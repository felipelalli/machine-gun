package br.fml.eti.machinegun.test.mocks;

import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.Consumer;
import br.fml.eti.machinegun.externaltools.QueueManager;

import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:19:53 PM
 */
public class FakeQueueManager implements QueueManager {
    private Map<String, BlockingQueue<byte[]>> queues
            = new HashMap<String,  BlockingQueue<byte[]>>();

    private Random random = new Random();
    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private boolean end = false;

    @Override
    public void putInAEmbeddedQueue(ArmyAudit armyAudit,
                                    String queueName, byte[] data)
            throws InterruptedException {

        long timeToWait = random.nextInt(100000);
        while (timeToWait > 0) { // HARD USE PROCESSOR
            timeToWait--;
        }

        getQueue(queueName).put(data);
    }

    private BlockingQueue<byte[]> getQueue(String queueName) {
        if (!queues.containsKey(queueName)) {
            queues.put(queueName, new LinkedBlockingQueue<byte[]>());
        }

        return queues.get(queueName);
    }

    int size = 0;

    @Override
    public void registerAConsumerInEmbeddedQueue(final ArmyAudit armyAudit,
                                                 final String queueName,
                                                 final Consumer consumer) {

        size++;

        Thread t = new Thread("mock consumer " + size + " of " + size) {
            public void run() {
                while (!end || getQueue(queueName).size() > 0) {
                    try {
                        byte[] data = getQueue(queueName).poll(5, TimeUnit.SECONDS);

                        if (data != null) {
                            consumer.consume(data);
                        }
                    } catch (InterruptedException e) {

                    }
                }

                armyAudit.rearSoldierDied(Thread.currentThread().getName());
            }
        };

        threads.add(t);
        t.start();
    }

    @Override
    public void killAllConsumers(String queueName) throws InterruptedException {
        for (Thread t : threads) {
            end = true;
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}
