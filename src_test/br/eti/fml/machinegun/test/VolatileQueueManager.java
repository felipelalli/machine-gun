/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun.test;

import br.eti.fml.machinegun.auditorship.ArmyAudit;
import br.eti.fml.machinegun.externaltools.Consumer;
import br.eti.fml.machinegun.externaltools.PersistedQueueManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:19:53 PM
 */
public class VolatileQueueManager implements PersistedQueueManager {
    private Map<String, BlockingQueue<byte[]>> queues
            = new HashMap<String, BlockingQueue<byte[]>>();

    int size = 0;

    private ArrayList<Thread> threads = new ArrayList<Thread>();
    private boolean end = false;

    public VolatileQueueManager(String ... queues) {
        for (String q : queues) {
            this.queues.put(q, new LinkedBlockingQueue<byte[]>());
        }
    }

    @Override
    public void putIntoAnEmbeddedQueue(ArmyAudit armyAudit,
                                    String queueName, byte[] data)
            throws InterruptedException {

        queues.get(queueName).put(data);
    }

    @Override
    public void registerANewConsumerInAnEmbeddedQueue(final ArmyAudit armyAudit,
                                                 final String queueName,
                                                 final Consumer consumer) {

        size++;
        final BlockingQueue<byte[]> queue = queues.get(queueName);

        Thread t = new Thread("consumer " + size + " of " + size) {
            public void run() {
                while (!end || queue.size() > 0) {
                    try {
                        byte[] data = queue.poll(1, TimeUnit.SECONDS);

                        if (data != null) {
                            consumer.consume(data);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                armyAudit.consumerHasBeenStopped(Thread.currentThread().getName());
            }
        };

        threads.add(t);
        t.start();
    }

    @Override
    public void killAllConsumers(String queueName) throws InterruptedException {
        end = true;

        for (Thread t : threads) {
            t.join();
        }
    }

    @Override
    public boolean isEmpty(String queueName) {
        return queues.get(queueName).size() == 0;
    }
}
