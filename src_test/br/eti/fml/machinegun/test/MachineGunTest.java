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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MachineGunTest {
    private volatile int sequential = 0;

    private static Set<Integer> processed
            = Collections.synchronizedSet(new HashSet<Integer>());

    public static void processed(int what) {
        processed.remove(what);
    }

    @Test
    public void memory() throws Exception {
        long before = System.currentTimeMillis();

        // create a fake "persisted" queue manager. It just use BlockingQueue
        PersistedQueueManager queueManager = new VolatileQueueManager();
        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);
        test(importedWeapons);

        long diff = System.currentTimeMillis() - before;
        System.out.println("memory test = " + diff + " millis");
    }

    @Test
    public void kyoto() throws Exception {
        long before = System.currentTimeMillis();

        KyotoCabinetBasedPersistedQueue queueManager
                = new KyotoCabinetBasedPersistedQueue(
                        new File("kyotodb"), 10240, "default queue");

        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);
        test(importedWeapons);

        long diff = System.currentTimeMillis() - before;
        System.out.println("kyoto test = " + diff + " millis");        
    }


    public void test(ImportedWeapons importedWeapons) throws Exception {
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
                dirtyWorkFactory, capsule);

        Thread[] threads = new Thread[10];

        // get a machine gun to strafe
        final MachineGun<Integer> machineGun
                = army.getANewMachineGun("default mission");        

        // creates 10 producers
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread("Producer " + i + " of " + threads.length) {
                public void run() {
                    // Produces 10.000 elements
                    for (int j = 0; j < 10000; j++) {
                        try {
                            int n = sequential++;
//                            System.out.println(Thread
//                                    .currentThread().getName()
//                                    + " will produce " + n);

                            processed.add(n);

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
            //System.out.println("waiting empty...");
            Thread.sleep(500);
        }

        // make all consumers die
        army.stopTheMission("default mission");

        // everything was well processed?
        Assert.assertTrue(processed.size() == 0);
    }
}
