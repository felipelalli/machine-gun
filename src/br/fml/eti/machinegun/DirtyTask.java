package br.fml.eti.machinegun;

import br.fml.eti.machinegun.auditorship.ArmyAudit;

/**
 * An expensive task.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:27:08 AM
 */
public interface DirtyTask<T> {
    /**
     * It can take a loooong long time!
     *
     * @param jobId The job identity to be called by <code>audit</code>.
     * @param dataToBeProcessed data ("bullet")
     * @param audit Details of processing. <b>The specific implementation
     *              MUST call the method {@link ArmyAudit#rearSoldierFinishesHisJob}</b>.
     */
    public void workOnIt(long jobId, ArmyAudit audit, T dataToBeProcessed);
}
