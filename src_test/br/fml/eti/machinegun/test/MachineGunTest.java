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
import br.fml.eti.machinegun.auditorship.NegligentAuditor;
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
        ArmyAudit armyAudit = new NegligentAuditor();
        PersistedQueueManager queueManager = new VolatileQueueManager();
        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);

        final Army army = new Army(armyAudit, importedWeapons);

        Factory<DirtyWork<Integer>> dirtyWorkFactory = new Factory<DirtyWork<Integer>>() {
            private ProcessIntegerSlowly processIntegerSlowly
                    = new ProcessIntegerSlowly();

            @Override
            public DirtyWork<Integer> buildANewInstance() throws BuildingException {
                return processIntegerSlowly;
            }
        };

        Capsule<Integer> capsule = new CapsuleInteger();

        army.startNewMission("default mission", "default queue",
                dirtyWorkFactory, capsule);

        Thread[] threads = new Thread[100];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread("Producer " + i + " of " + threads.length) {
                public void run() {
                    MachineGun<Integer> machineGun = army.getANewMachineGun();

                    for (int j = 0; j < 10000; j++) {
                        try {
                            int n = sequential++;
                            processed.add(n);
                            machineGun.fire(n, "default mission");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        army.stopTheMission("default mission");

        Assert.assertTrue(processed.size() == 0);
    }
}
