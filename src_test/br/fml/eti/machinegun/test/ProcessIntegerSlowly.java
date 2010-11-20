package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.DirtyWork;
import br.fml.eti.machinegun.auditorship.ArmyAudit;

import java.util.Random;

/**
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:54:17 PM
 */
public class ProcessIntegerSlowly extends Factory<DirtyWork<Integer>> {
    private Random random = new Random();

    private DirtyWork<Integer> dirtyWork = new DirtyWork<Integer>() {
        @Override
        public void workOnIt(long jobId, String soldierName,
                             ArmyAudit audit, Integer dataToBeProcessed) {

            Integer time = random.nextInt(2);
            System.out.println("*** Will process " + dataToBeProcessed
                    + " for " + time + " millis...");

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("*** " + dataToBeProcessed + " was processed!!");
            audit.aConsumerHasBeenFinishedHisJob(jobId, soldierName, true, null, "OK!");
        }
    };

    @Override
    public DirtyWork<Integer> buildANewInstance() throws BuildingException {
        return dirtyWork;
    }
}
