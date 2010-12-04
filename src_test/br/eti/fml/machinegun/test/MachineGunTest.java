/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun.test;

import br.eti.fml.behavior.BuildingException;
import br.eti.fml.behavior.Factory;
import br.eti.fml.implementations.KyotoCabinetBasedPersistedQueue;
import br.eti.fml.machinegun.Army;
import br.eti.fml.machinegun.Capsule;
import br.eti.fml.machinegun.DirtyWork;
import br.eti.fml.machinegun.MachineGun;
import br.eti.fml.machinegun.auditorship.ArmyAudit;
import br.eti.fml.machinegun.auditorship.NegligentAuditor;
import br.eti.fml.machinegun.externaltools.ImportedWeapons;
import br.eti.fml.machinegun.externaltools.PersistedQueueManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class MachineGunTest {
    private int sequential = 0;
    private static int processed;

    private static final int THREADS = 4;
    private static final int TESTS = 1000000;

    public synchronized static void increase(int what) {
        processed += what;
    }

    public synchronized static void decrease(int what) {
        processed -= what;
    }

    @Test
    public void memory() throws Exception {
        long before = System.currentTimeMillis();

        // create a fake "persisted" queue manager. It just use BlockingQueue
        PersistedQueueManager queueManager
                = new VolatileQueueManager("default queue");
        
        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);
        test(importedWeapons);

        long diff = System.currentTimeMillis() - before;
        System.out.println("\nmemory test = " + diff + " ms; "
                + ((double) diff / ((double) (THREADS * TESTS))) + " ms each");
    }

    @Test
    public void kyoto() throws Exception {
        //for (int i = 0; i < 300; i++) {
            long before = System.currentTimeMillis();

            KyotoCabinetBasedPersistedQueue queueManager
                    = new KyotoCabinetBasedPersistedQueue(
                            new File("kyotodb"), "default queue");

            ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);
            test(importedWeapons);

            long diff = System.currentTimeMillis() - before;
        
            System.out.println("\nkyoto test = " + diff + " ms; "
                    + ((double) diff / ((double) (THREADS * TESTS))) + " ms each");
        //}
    }


    public void test(ImportedWeapons importedWeapons) throws Exception {
        processed = 0;

        PersistedQueueManager queueManager = importedWeapons.getQueueManager();

        // create an ArmyAudit that just show to standard output
        //ArmyAudit armyAudit = new SystemOutAudit();
        ArmyAudit armyAudit = new NegligentAuditor();

        // create an Army to make machine guns
        final Army army = new Army(armyAudit, importedWeapons);

        // here is the dirty task producer
        // ProcessIntegerSlowly simulates a "dirty work" and just sleep randomly
        Factory<DirtyWork<Integer>> dirtyWorkFactory = new Factory<DirtyWork<Integer>>() {
            private ProcessIntegerSlowly processIntegerSlowly
                    = new ProcessIntegerSlowly();

            @Override
            public DirtyWork<Integer> buildANewInstance() throws BuildingException {
                return processIntegerSlowly;
            }
        };

        // This capsule uses Google Protocol Buffers to convert Integer
        // into an array of bytes and vice-versa
        Capsule<Integer> capsule = new CapsuleInteger();

        // start the mission called "default mission" working on "default queue"
        army.startNewMission("default mission", "default queue",
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
                            final int n = sequential++;
//                            System.out.println(Thread
//                                    .currentThread().getName()
//                                    + " will produce " + n);

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
        if (processed != 0) {
            System.err.println("ERROR size != 0: " + processed);
        }
        
        Assert.assertTrue(processed == 0);
    }
}
