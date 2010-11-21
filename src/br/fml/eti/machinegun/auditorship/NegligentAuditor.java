package br.fml.eti.machinegun.auditorship;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.WrongCapsuleException;

/**
 * <p>A {@link NegligentAuditor} do nothing. Use it if you think the
 * monitor time is taking too much unnecessary time.
 * </p>
 * <pre>
     ___
    /_\   z z Z z Z Z Z z z
   <*,*>
   [`-']
   -"-"-
 </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:27:30 PM
 */
public class NegligentAuditor implements ArmyAudit {
    @Override
    public void aConsumerStartsHisJob(long jobId, String consumerName) {

    }

    @Override
    public void aConsumerHasBeenFinishedHisJob(long jobId, String consumerName,
                                          boolean success,
                                          BuildingException exception,
                                          String resultDetails) {

    }

    @Override
    public void updateCurrentBufferSize(int newSize, int maxSize) {

    }

    @Override
    public void consumerIsReady(String consumerName) {

    }

    @Override
    public void consumerHasBeenStopped(String consumerName) {
       
    }

    @Override
    public void errorOnDataSerialization(WrongCapsuleException e) {
        
    }

    @Override
    public void errorWhenPuttingIntoAnEmbeddedQueue(Exception e) {

    }

    @Override
    public void errorWhenRegisteringANewConsumerInAnEmbeddedQueue(Exception e) {

    }
}
