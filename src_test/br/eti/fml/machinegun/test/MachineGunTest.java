/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun.test;

import br.eti.fml.behavior.BuildingException;
import br.eti.fml.behavior.Factory;
import br.eti.fml.machinegun.Army;
import br.eti.fml.machinegun.Capsule;
import br.eti.fml.machinegun.DirtyWork;
import br.eti.fml.machinegun.MachineGun;
import br.eti.fml.machinegun.auditorship.ArmyAudit;
import br.eti.fml.machinegun.externaltools.PersistedQueueManager;
import br.eti.fml.machinegun.tools.GenericCapsuleForLazyPeople;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MachineGunTest {
    private static AtomicInteger sequential = new AtomicInteger(0);
    private static AtomicInteger processed = new AtomicInteger(0);

    private static final int THREADS = 4;
    private static final int TESTS = 10000;

    private static Random random = new Random();

    public static void increase(int what) {
        processed.addAndGet(what);
    }

    public static void decrease(int what) {
        processed.addAndGet(-what);
    }

    public static void main(String[] args) throws Exception {
        long before = System.currentTimeMillis();

        // create a fake "persisted" queue manager. It just use BlockingQueue
        PersistedQueueManager queueManager = new VolatileQueueManager("default queue");
        test(queueManager);

        long diff = System.currentTimeMillis() - before;
        System.out.println("\nmemory test = " + diff + " ms; "
                + ((double) diff / ((double) (THREADS * TESTS))) + " ms each");
    }

    public static void test(PersistedQueueManager queueManager) throws Exception {
        processed.set(0);

        // create an ArmyAudit that just show to standard output
        ArmyAudit armyAudit = new SystemOutAudit();
        //ArmyAudit armyAudit = new NegligentAuditor();

        // create an Army to make machine guns
        final Army army = new Army(armyAudit, queueManager);

        // here is the dirty task producer
        // ProcessIntegerSlowly simulates a "dirty work" and just sleep randomly
        Factory<DirtyWork<Integer>> dirtyWorkFactory = new Factory<DirtyWork<Integer>>() {
            @Override
            public DirtyWork<Integer> buildANewInstance() throws BuildingException {
                return new DirtyWork<Integer>() {
                    @Override
                    public void workOnIt(long jobId, String consumerName, ArmyAudit audit, Integer dataToBeProcessed) {
                        Integer time = random.nextInt(2);

                        System.out.println("*** Will process " + dataToBeProcessed
                                + " for " + time + " millis...");

                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        decrease(dataToBeProcessed);

                        audit.aConsumerHasBeenFinishedHisJob(jobId, consumerName, true, null, "OK!");
                    }
                };
            }
        };

        // This capsule uses the Java default serialization implementation
        Capsule<Integer> capsule = new GenericCapsuleForLazyPeople<Integer>();

        // start the mission called "default mission" working on "default queue"
        army.startANewMission("default mission", "default queue",
                dirtyWorkFactory, capsule, 100000, 4, 12);

        Thread[] threads = new Thread[THREADS];

        // get a machine gun to strafe
        final MachineGun<Integer> machineGun
                = army.getANewMachineGun("default mission");        

        for (int i = 0; i < threads.length; i++) {
            final int y = i;

            threads[i] = new Thread("Producer " + i + " of " + threads.length) {
                public void run() {
                    for (int j = 0; j < TESTS; j++) {
                        if (j % 100000 == 0) {
                            System.out.print("" + y);
                        }

                        try {
                            final int n = sequential.incrementAndGet();
                            System.out.println(Thread
                                    .currentThread().getName()
                                    + " will produce " + n);

                            increase(n);

                            // strafe
                            machineGun.fire(n);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };

            // start the producer
            threads[i].start();
        }

        // wait the producers die
        for (Thread thread : threads) {
            thread.join();
        }

        // wait the queue is empty
        while (!queueManager.isEmpty("default queue")) {
            System.out.print("?");
            Thread.sleep(1000);
        }

        // make all consumers die
        army.stopTheMission("default mission");

        // everything was well processed?
        if (processed.get() != 0) {
            System.err.println("ERROR size != 0: " + processed);
        }

        assert processed.get() == 0;
    }
}
