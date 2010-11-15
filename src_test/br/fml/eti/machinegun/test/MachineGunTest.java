package br.fml.eti.machinegun.test;

import br.fml.eti.machinegun.Army;
import br.fml.eti.machinegun.MachineGun;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;
import br.fml.eti.machinegun.test.mocks.FakeQueueManager;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Random;

public class MachineGunTest {
    @Test public void test() throws Exception {
        Army army = new Army(new SystemOutAudit(),
                new ImportedWeapons(new FakeQueueManager()));

        army.startNewMission("default mission",
                "default queue", new ProcessIntegerSlowly(),
                new CapsuleInteger(), Army.DEFAULT_BATTALION_SIZE,
                Army.SMART_NUMBER_OF_SOLDIERS, Army.SMART_NUMBER_OF_SOLDIERS);

        MachineGun<Integer> machineGun = army.getANewMachineGun();

        for (int i = 0; i < 50000; i++) {
            machineGun.fire(i, "default mission");
        }

        army.stopTheMission("default mission");
    }
}
