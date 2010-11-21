package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.*;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;
import br.fml.eti.machinegun.externaltools.PersistedQueueManager;
import org.junit.Test;

public class MachineGunTest {
    @Test   
    public void test() throws Exception {
        ArmyAudit armyAudit = new SystemOutAudit();
        PersistedQueueManager queueManager = new VolatileQueueManager();
        ImportedWeapons importedWeapons = new ImportedWeapons(queueManager);

        Army army = new Army(armyAudit, importedWeapons);

        Factory<DirtyWork<Integer>> dirtyWorkFactory = new Factory<DirtyWork<Integer>>() {
            @Override
            public DirtyWork<Integer> buildANewInstance() throws BuildingException {
                return new ProcessIntegerSlowly();
            }
        };

        Capsule<Integer> capsule = new CapsuleInteger();

        army.startNewMission("default mission",
                "default queue", dirtyWorkFactory,
                capsule);

        MachineGun<Integer> machineGun = army.getANewMachineGun();

        for (int i = 0; i < 50000; i++) {
            machineGun.fire(i, "default mission");
        }

        army.stopTheMission("default mission");
    }
}
