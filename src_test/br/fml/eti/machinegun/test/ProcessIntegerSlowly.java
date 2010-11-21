/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun.test;

import br.fml.eti.machinegun.DirtyWork;
import br.fml.eti.machinegun.auditorship.ArmyAudit;

import java.util.Random;

/**
 * Simulate a Integer processing using a random sleep of 0 to 2 millis. 
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:54:17 PM
 */
public class ProcessIntegerSlowly implements DirtyWork<Integer> {
    @Override
    public void workOnIt(long jobId, String consumerName,
                         ArmyAudit audit, Integer dataToBeProcessed) {

        MachineGunTest.processed(dataToBeProcessed);
        audit.aConsumerHasBeenFinishedHisJob(jobId, consumerName, true, null, "OK!");
    }
}
