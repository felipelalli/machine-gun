package br.fml.eti.machinegun;

import br.fml.eti.machinegun.auditorship.ArmyAudit;

/**
 * An expensive task.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:27:08 AM
 */
public interface DirtyWork<T> {
    /**
     * <p>This operation can take a loooong long time!</p>
     * <p>
     * <b>Important:</b> it MUST call
     * {@link ArmyAudit#aConsumerHasBeenFinishedHisJob} after
     * the dirty work finishes.
     * </p>
     *
     * @param jobId The job identity to be called by <code>audit</code>.
     * @param consumerName To be used by the <code>audit</code>.
     * @param dataToBeProcessed data (the "bullet")
     * @param audit Interface to intercept details of processing.
     *              <b>The specific implementation
     *              MUST call the method {@link ArmyAudit#aConsumerHasBeenFinishedHisJob}</b>.
     */
    public void workOnIt(long jobId, String consumerName,
                         ArmyAudit audit, T dataToBeProcessed);
}
