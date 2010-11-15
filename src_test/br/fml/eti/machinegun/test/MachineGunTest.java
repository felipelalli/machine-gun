package br.fml.eti.machinegun.test;

import br.fml.eti.machinegun.Army;
import br.fml.eti.machinegun.MachineGun;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;
import br.fml.eti.machinegun.test.mocks.FakeQueueManager;
import org.junit.Test;

import java.util.Random;

public class MachineGunTest {
    @Test public void test() throws Exception {
        Random random = new Random();

        Army army = new Army(new SystemOutAudit(),
                new ImportedWeapons(new FakeQueueManager()));

        army.startNewMission("default mission",
                "default queue", new ProcessIntegerSlowly(),
                new CapsuleInteger(), Army.DEFAULT_BATTALION_SIZE,
                Army.SMART_NUMBER_OF_SOLDIERS, Army.SMART_NUMBER_OF_SOLDIERS);

        MachineGun<Integer> machineGune = army.getANewMachineGun();

        machineGune.fire(random.nextInt(), "default mi");
    }
}
