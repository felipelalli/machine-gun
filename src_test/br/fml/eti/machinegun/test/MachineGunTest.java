/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.Army;
import br.fml.eti.machinegun.Capsule;
import br.fml.eti.machinegun.DirtyWork;
import br.fml.eti.machinegun.MachineGun;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;
import br.fml.eti.machinegun.externaltools.PersistedQueueManager;
import org.junit.Assert;
import org.junit.Test;

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
    public void test() throws Exception {
        // create an ArmyAudit that just show to standard output
        ArmyAudit armyAudit = new SystemOutAudit();

        // create a fake "persisted" queue manager. It just use BlockingQueue
        PersistedQueueManager queueManager = new VolatileQueueManager();
        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);

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

        Thread[] threads = new Thread[100];

        // get a machine gun to strafe
        final MachineGun<Integer> machineGun
                = army.getANewMachineGun("default mission");        

        // creates 100 producers
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread("Producer " + i + " of " + threads.length) {
                public void run() {
                    // Produces 10.000 elements
                    for (int j = 0; j < 10000; j++) {
                        try {
                            int n = sequential++;
                            System.out.println(Thread
                                    .currentThread().getName()
                                    + " will produce " + n);

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

        // make all consumers die
        army.stopTheMission("default mission");

        // everything was well processed?
        Assert.assertTrue(processed.size() == 0);
    }
}
