package br.fml.eti.machinegun;

import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.auditorship.LazyArmyAudit;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.HashMap;
import java.util.Map;

/**
 * Produces {@link MachineGun machine guns}. Also, organize the soldiers
 * ({@link Thread threads}) to let the bullet (data) reach the right destination. 
 * Bullets are data, and bullets are from a specific type.
 * A machine gun is a way to make this process very fast, asynchronously.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:15:37 AM
 */
public class Army extends Factory<MachineGun> {
    private Map<String, Mission> missions;
    private ArmyAudit armyAudit;
    private ImportedWeapons importedWeapons;

    /**
     * The default is {@value}.
     */
    public static final int DEFAULT_BUFFER_SIZE = 256;

    /**
     * It will use {@link Runtime#availableProcessors()}<code> - 1</code>
     * for <code>frontLineNumberOfSoldiers</code> and
     * {@link Runtime#availableProcessors()}<code> * 3</code>
     * for <code>rearNumberOfSoldiers</code>. 
     */
    public static final int SMART_NUMBER_OF_CONSUMERS = 0;

    /**
     * Create a new Army. See {@link #startNewMission} to have some fun.
     * 
     * @param armyAudit If you want to take control of your Army. See
     *                  {@link LazyArmyAudit} if you don't need of
     *                  auditorship. This parameter don't accept <code>null</code>.
     *
     * @param importedWeapons Specific implementations.
     */
    public Army(ArmyAudit armyAudit, ImportedWeapons importedWeapons) {
        this.missions = new HashMap<String, Mission>();

        if (armyAudit == null || importedWeapons == null) {
            throw new NullPointerException("Internal error: armyAudit and"
                    + " importedWeapons can't be null!");
        }

        this.armyAudit = armyAudit;
        this.importedWeapons = importedWeapons;
    }

    /**
     * Associates a {@link Mission} with a {@link DirtyTask dirty task}
     * {@link Factory factory}. You have to use different queues to each
     * dirty task factory; in other words: each queue will transport only
     * the same kind of data. This function will create
     *
     * @param missionName The mission name. Can be the same of queueName.
     * @param queueName The queue name where the "bullets" (data) from
     *                  the {@link MachineGun machine guns} will be transported
     *                  to the final {@link Target target}. If you are using
     *                  a JMS based queue, it is the
     *                  <code>javax.jms.Queue#getQueueName()</code>.
     *
     * @param dirtyTaskFactory the associated factory of dirties tasks. When the
     *                         "bullet" (data) reaches the target, the
     *                         {@link DirtyTask dirty task} will be executed
     *                         on the data.
     *
     * @param capsule A {@link Capsule} is a way to keep the
     *                "bullet" (data) intact through the way to
     *                the target. It can convert data to a byte array and
     *                vice-versa.
     *
     * @param battalionSize It is the <b>internal buffer size</b>.
     *                      If the buffer is full, the {@link MachineGun#fire}
     *                      function will be blocked until the consumers
     *                      can drain the volume. You can use
     *                      {@link #DEFAULT_BUFFER_SIZE}. Set
     *                      high values if you have high available memory
     *                      and don't care so much about lost some data.
     *                      <i>Remember that what is on the buffer will not be
     *                      persisted. If is important to persist EVERYTHING,
     *                      set this parameter to <b>1</b></i>.
     *
     * @param frontLineNumberOfSoldiers The number of thread consumers to read from
     *                                   internal buffer and put on internal
     *                                   queue. Use {@link #SMART_NUMBER_OF_CONSUMERS}
     *                                   to make the MachineGun calculates based
     *                                   on your {@link Runtime#availableProcessors()
     *                                   available processors}.
     *
     * @param rearNumberOfSoldiers The number of embedded queue thread consumers.
     *                              This consumers will do the dirty and hard workOnIt.
     *                              Use {@link #SMART_NUMBER_OF_CONSUMERS}
     *                              to make the MachineGun calculates based
     *                              on your {@link Runtime#availableProcessors()
     *                              available processors}.
     */
    public <T> void startNewMission(String missionName, String queueName,
                               Factory<DirtyTask<T>> dirtyTaskFactory,
                               Capsule<T> capsule,
                               int battalionSize,
                               int frontLineNumberOfSoldiers,
                               int rearNumberOfSoldiers) {

        Target<T> target = new Target<T>(queueName, dirtyTaskFactory);
        Mission<T> mission = new Mission<T>(armyAudit, importedWeapons,
                target, capsule, battalionSize,
                frontLineNumberOfSoldiers, rearNumberOfSoldiers);

        this.missions.put(missionName, mission);

        mission.startTheMission();
    }

    public void finalize() throws Throwable {
        for (String mission : this.missions.keySet()) {
            this.stopTheMission(mission);
        }

        super.finalize();
    }

    public void stopTheMission(String missionName) throws InterruptedException {
        this.missions.get(missionName).stopTheMission();
    }

    @Override
    /**
     * @see #getANewMachineGun()
     */
    public MachineGun buildANewInstance() {
        return this.getANewMachineGun();
    }

    /**
     * Produces a shiny new machine gun.
     * 
     * @return a new machine gun to be used immediately.
     */
    public <T> MachineGun<T> getANewMachineGun() {
        return new MachineGun<T>() {
            @Override
            public void fire(T bullet, String missionName)
                    throws UnregisteredMissionException, InterruptedException {

                Mission mission = missions.get(missionName);

                if (mission == null) {
                    throw new UnregisteredMissionException("The mission '"
                            + missionName
                            + "' was not registered yet! See 'startNewMission(...)'"
                            + " function.");
                } else {
                    mission.fire(bullet);
                }
            }
        };
    }
}
