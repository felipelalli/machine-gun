package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.DirtyTask;
import br.fml.eti.machinegun.auditorship.ArmyAudit;

import java.util.Random;

/**
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:54:17 PM
 */
public class ProcessIntegerSlowly extends Factory<DirtyTask<Integer>> {
    private Random random = new Random();

    private DirtyTask<Integer> dirtyTask = new DirtyTask<Integer>() {
        @Override
        public void workOnIt(long jobId, String soldierName,
                             ArmyAudit audit, Integer dataToBeProcessed) {

            Integer time = random.nextInt(1000);
            System.out.println("*** Will process " + dataToBeProcessed
                    + " for " + time + " millis...");

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("*** " + dataToBeProcessed + " was processed!!");
            audit.rearSoldierFinishesHisJob(jobId, soldierName, true, null, "OK!");
        }
    };

    @Override
    public DirtyTask<Integer> buildANewInstance() throws BuildingException {
        return dirtyTask;
    }
}
