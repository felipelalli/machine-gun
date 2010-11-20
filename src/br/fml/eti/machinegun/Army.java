package br.fml.eti.machinegun;

import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Produces {@link MachineGun machine guns}. Also, organize the thread consumers
 * to let the bullet (data) reach the specified target. 
 * Bullets are data, and bullets are from a specific type.
 * A <i>machine gun</i> is a way to make this processing <u>very fast</u>,
 * asynchronously.</p>
 * <p>Before of take a new machine gun, don't forget to
 * {@link #startNewMission start a mission}.</p>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:15:37 AM
 */
public class Army extends Factory<MachineGun> {
    private Map<String, Mission> missions;
    private ArmyAudit armyAudit;
    private ImportedWeapons importedWeapons;

    /**
     * The default is {@value}. Battalion size is the internal
     * buffer size.
     */
    public static final int DEFAULT_BATTALION_SIZE = 1024;

    /**
     * It will use {@link Runtime#availableProcessors()}<code> * 2</code>
     * for <code>frontLineNumberOfSoldiers</code> and
     * {@link Runtime#availableProcessors()}<code> * 5</code>
     * for <code>rearNumberOfSoldiers</code>. The soldiers are the
     * threads to consume the volume of data.
     */
    public static final int SMART_NUMBER_OF_SOLDIERS = 0;

    /**
     * Create a new Army. See {@link #startNewMission} to have some fun.
     * 
     * @param armyAudit If you want to take control of your Army. See
     *                  {@link br.fml.eti.machinegun.auditorship.NegligentAuditor} if you don't need of
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
     * Associates a {@link Mission} with a {@link DirtyWork dirty work}
     * {@link Factory factory}. You have to use different queues to each
     * dirty work factory; in other words: each queue will transport only
     * the same kind of data. This function will create
     *
     * @param missionName The mission name. Can be the same of queueName.
     * @param queueName The queue name where the "bullets" (data) from
     *                  the {@link MachineGun machine guns} will be transported
     *                  to the final {@link Target target}. If you are using
     *                  a JMS based queue, it is the
     *                  <code>javax.jms.Queue#getQueueName()</code>.
     *
     * @param dirtyWorkFactory the associated factory of dirties tasks. When the
     *                         "bullet" (data) reaches the target, the
     *                         {@link DirtyWork dirty work} will be executed
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
     *                      {@link #DEFAULT_BATTALION_SIZE}. Set
     *                      high values if you have high available memory
     *                      and don't care so much about lost some data.
     *                      <i>Remember that what is on the buffer will not be
     *                      persisted. If is important to persist EVERYTHING,
     *                      set this parameter to <b>1</b></i>.
     *
     * @param frontLineNumberOfSoldiers The number of thread consumers to read from
     *                                   internal buffer and put on internal
     *                                   queue. Use {@link #SMART_NUMBER_OF_SOLDIERS}
     *                                   to make the MachineGun calculates based
     *                                   on your {@link Runtime#availableProcessors()
     *                                   available processors}.
     *
     * @param rearNumberOfSoldiers The number of embedded queue thread consumers.
     *                              This consumers will do the dirty and hard workOnIt.
     *                              Use {@link #SMART_NUMBER_OF_SOLDIERS}
     *                              to make the MachineGun calculates based
     *                              on your {@link Runtime#availableProcessors()
     *                              available processors}.
     */
    public <T> void startNewMission(String missionName, String queueName,
                               Factory<DirtyWork<T>> dirtyWorkFactory,
                               Capsule<T> capsule,
                               int battalionSize,
                               int frontLineNumberOfSoldiers,
                               int rearNumberOfSoldiers) {

        Target<T> target = new Target<T>(queueName, dirtyWorkFactory);
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

    public void stopTheMission(String missionName) throws
            InterruptedException, UnregisteredMissionException {

        if (!this.missions.containsKey(missionName)) {
            throw new UnregisteredMissionException(missionName);
        } else {
            this.missions.get(missionName).stopTheMission();
        }
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

                @SuppressWarnings(value = "unchecked")
                Mission<T> mission = missions.get(missionName);

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
