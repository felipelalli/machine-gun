package br.fml.eti.machinegun;

import br.fml.eti.machinegun.auditorship.ArmyAudit;

/**
 * <p>
 * Someone has to do the dirty work.
 * </p>
 * <pre>
            ||||||||||||||
           =              \       ,
           =               |
          _=            ___/
         / _\           (o)\
        | | \            _  \
        | |/            (____)
         \__/          /   |
          /           /  ___)
         /    \       \    _)                       )
        \      \           /                       (
      \/ \      \_________/   |\_________________,_ )
       \/ \      /            |     ==== _______)__)
        \/ \    /           __/___  ====_/
         \/ \  /           (O____)\\_(_/
                          (O_ ____)
                           (O____)

 * </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:27:08 AM
 */
public interface DirtyWork<T> {
    /**
     * <p>This operation can take a loooong <u>long</u> time!</p>
     * <p>
     * <b>Important:</b> it MUST call
     * {@link ArmyAudit#aConsumerHasBeenFinishedHisJob} after
     * the end of execution.
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
