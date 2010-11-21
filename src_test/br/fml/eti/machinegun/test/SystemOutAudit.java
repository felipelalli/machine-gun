package br.fml.eti.machinegun.test;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.WrongCapsuleException;
import br.fml.eti.machinegun.auditorship.ArmyAudit;

public class SystemOutAudit implements ArmyAudit {
    @Override
    public void aConsumerStartsHisJob(long jobId, String consumerName) {
        System.out.println(consumerName + ": starts his job");
    }

    @Override
    public void aConsumerHasBeenFinishedHisJob(long jobId, String consumerName,
                                          boolean success,
                                          BuildingException exception,
                                          String resultDetails) {

        System.out.println(consumerName + ": finishes his job: "
                + success + ", exception: " + exception + ", resultDetails: " + resultDetails);
    }

    @Override
    public void updateCurrentBufferSize(int newSize, int maxSize) {
        System.out.println(newSize + " of " + maxSize + ": "
                + (newSize * 100 / maxSize) + "%");
    }

    @Override
    public void consumerIsReady(String consumerName) {
        System.out.println(consumerName + " is ready!");
    }

    @Override
    public void consumerHasBeenStopped(String consumerName) {
        System.out.println(consumerName + " died!");
    }

    @Override
    public void errorOnDataSerialization(WrongCapsuleException e) {
        System.out.println("Error: " + e);
        e.printStackTrace();
    }

    @Override
    public void errorWhenPuttingIntoAnEmbeddedQueue(Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();
    }

    @Override
    public void errorWhenRegisteringANewConsumerInAnEmbeddedQueue(Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();
    }
}
