package br.fml.eti.machinegun.auditorship;

import br.fml.eti.behavior.BuildingException;

/**
 * Everything that happens in {@link br.fml.eti.machinegun.Army}
 * will related here.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:17:13 PM
 */
public interface ArmyAudit {
    /**
     * When a "rear soldier" &ndash; i.e., a embedded
     * queue consumer &ndash; found a element
     * to consume and will start to do the
     * {@link br.fml.eti.machinegun.DirtyTask dirty task}.
     *
     * @param soldierName A soldier name like "Rear soldier 2".
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between the start and end. 
     */
    void rearSoldierStartsHisJob(long jobId, String soldierName);

    /**
     * When a "rear soldier" &ndash; i.e., a embedded
     * queue consumer &ndash; finished to do the
     * {@link br.fml.eti.machinegun.DirtyTask dirty task}.
     *
     * @param soldierName A soldier name like "Rear soldier 2".
     * @param success <code>true</code> if everything was OK
     * @param exception if <code>success</code> is <code>false</<code> this exception
     *          can be different of <code>null</code>. It will be <code>null</code>
     *          if <code>success</code> is <code>true</code>.
     * @param message A message giving details of operation.
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between the start and end. 
     */
    void rearSoldierFinishesHisJob(long jobId, String soldierName,
                                   boolean success, BuildingException exception,
                                   String message);

    /**
     * If <code>newSize / maxSize</code> is near to 100% it means
     * that the internal buffer is overloaded and a call to
     * {@link br.fml.eti.machinegun.MachineGun#fire} can block. 
     *
     * @param newSize Elements in internal buffer to be consumed.
     * @param maxSize Maximum capacity of the internal buffer.
     */
    void updateBattalionSize(int newSize, int maxSize);

    /**
     * The thread that will consume the internal buffer is ready.
     * @param soldierName The thread's name.
     */
    void frontLineSoldierIsReady(String soldierName);

    /**
     * When the consumer of embedded queue is ready to consume.
     * @param soldierName A soldier name like "Rear soldier 2".
     */
    void rearSoldierIsReady(String soldierName);

    /**
     * When a "front line soldier" &ndash; i.e., a internal
     * buffer consumer &ndash; found a element
     * to consume and will start to put it into the embedded queue.
     *
     * @param soldierName A soldier name like "Front line soldier 3".
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between the start and end.
     */
    void frontLineSoldierStartsHisJob(long jobId, String soldierName);

    /**
     * When a "front line soldier" &ndash; i.e., a internal
     * buffer consumer &ndash; finished to put the data into the embedded queue.
     *
     * @param soldierName A soldier name like "Front line soldier 3".
     * @param jobId To identify the job. Useful if you want to measure the
     *              time between the start and end. 
     */
    void frontLineSoldierFinishesHisJob(long jobId, String soldierName);

    /**
     * It happens when a mission finished.
     * @param soldierName The thread's name.
     */
    void frontLineSoldierDied(String soldierName);
}
