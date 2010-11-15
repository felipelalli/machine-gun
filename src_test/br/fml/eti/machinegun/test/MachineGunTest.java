package br.fml.eti.machinegun.test;

import br.fml.eti.machinegun.Army;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;
import br.fml.eti.machinegun.test.mocks.FakeQueueManager;
import org.junit.Test;

public class MachineGunTest {
    @Test public void test() {
        Army army = new Army(new SystemOutAudit(),
                new ImportedWeapons(new FakeQueueManager()));
    }
}
