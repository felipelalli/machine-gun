/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun;

import br.fml.eti.behavior.Factory;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Produces {@link MachineGun machine guns}. Also,
 * organize the thread consumers
 * to let the bullet (data) reach the specified {@link Target target}.
 * Bullets are data, and bullets are from a specific type.
 * A <i>machine gun</i> is a way to make this processing <u>very fast</u>,
 * asynchronously.</p>
 * <p>Before {@link #getANewMachineGun to take a new machine gun},
 * don't forget to
 * {@link #startNewMission start a mission}.</p>
 * <pre>
   [ O ]
     \ \      p
      \ \  \o/
       \ \--'---_
       /\ \   / ~~\_
 ./---/__|=/_/------//~~~\
/___________________/O   O \
(===(\_________(===(Oo o o O)          W<
 \~~~\____/     \---\Oo__o--
   ~~~~~~~       ~~~~~~~~~~
 </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:15:37 AM
 */
public class Army extends Factory<MachineGun> {
    private Map<String, Mission> missions;
    private String lastUsedMission = "";
    private ArmyAudit armyAudit;
    private ImportedWeapons importedWeapons;

    /**
     * Create a new Army. See {@link #startNewMission} to have some fun.
     * 
     * @param armyAudit If you want to take control of your Army. See
     *                  {@link br.fml.eti.machinegun.auditorship.NegligentAuditor}
     *                  if you don't need of an
     *                  auditorship. This parameter don't
     *                  accept <code>null</code>.
     *
     * @throws IllegalArgumentException if some parameters are <code>null</code>.
     *
     * @param importedWeapons Specific implementations.
     */
    public Army(ArmyAudit armyAudit, ImportedWeapons importedWeapons) {
        this.missions = new HashMap<String, Mission>();

        if (armyAudit == null || importedWeapons == null) {
            throw new IllegalArgumentException("Internal error: armyAudit and"
                    + " importedWeapons can't be null!");
        }

        this.armyAudit = armyAudit;
        this.importedWeapons = importedWeapons;
    }

    /**
     * This function will create and start a {@link Mission mission}
     * asynchronously.
     * 
     * This associates a {@link Mission} with a {@link DirtyWork dirty work}
     * {@link Factory factory}. You have to use different queues to each
     * kind of work; in other words: each queue will transport only
     * ONE kind of data.
     *
     * @param missionName An arbitrary mission name. Can be the same of queueName.
     * @param queueName The queue name where the "bullets" (data) from
     *                  the {@link MachineGun machine guns} will be transported
     *                  to the final {@link Target target}. If you are using
     *                  a JMS based queue, it is the
     *                  <code>javax.jms.Queue#getQueueName()</code>. Remember
     *                  that this name is not an arbitrary name, you have to
     *                  configure it in some place of your
     *                  {@link br.fml.eti.machinegun.externaltools.PersistedQueueManager}
     *                  specific implementation.
     *
     * @param dirtyWorkFactory the associated factory of dirty works. When the
     *                         "bullet" (data) reaches the target, the
     *                         {@link DirtyWork dirty work} will be executed
     *                         using the data as parameter.
     *
     * @param capsule A {@link Capsule} is a way to keep the
     *                "bullet" (data) intact through the way to
     *                the target. It have to be able to convert a data
     *                to a byte array and vice-versa. If you are really lazy,
     *                see {@link br.fml.eti.machinegun.util.GenericCapsuleForLazyPeople}.
     *
     * @param volatileBufferSize It is the <b>internal buffer size</b>.
     *                      If the buffer is full, the {@link MachineGun#fire}
     *                      function will be blocked until the consumers
     *                      could drain the volume. You can use
     *                      {@link Mission#DEFAULT_VOLATILE_BUFFER_SIZE}. Set
     *                      high values if you have high available memory
     *                      and don't care so much about lost some data.
     *                      <i>Remember that what is on the buffer will not be
     *                      persisted. If is important to persist EVERYTHING,
     *                      set this parameter to <big><b>1</b></big></i>.
     *
     * @param numberOfBufferConsumers The number of thread consumers to read from
     *                                   internal buffer and put on the persisted
     *                                   queue. Use {@link Mission#SMART_NUMBER_OF_CONSUMERS}
     *                                   to make the function calculates based
     *                                   on your {@link Runtime#availableProcessors()
     *                                   available processors}.
     *
     * @param numberOfPersistedQueueConsumers The number of embedded queue thread consumers.
     *                              This consumers will do the dirty and hard work.
     *                              Use {@link Mission#SMART_NUMBER_OF_CONSUMERS}
     *                              to make the function calculates based
     *                              on your {@link Runtime#availableProcessors()
     *                              available processors}.
     */
    public <T> void startNewMission(String missionName, String queueName,
            Factory<DirtyWork<T>> dirtyWorkFactory,
            Capsule<T> capsule, int volatileBufferSize,
            int numberOfBufferConsumers, int numberOfPersistedQueueConsumers) {

        Target<T> target = new Target<T>(queueName, dirtyWorkFactory);
        Mission<T> mission = new Mission<T>(armyAudit, importedWeapons,
                target, capsule, volatileBufferSize,
                numberOfBufferConsumers, numberOfPersistedQueueConsumers);

        this.missions.put(missionName, mission);
        mission.startTheMission();
        lastUsedMission = missionName;
    }

    /**
     * Call {@link #startNewMission} using default values to
     * <code>volatileBufferSize</code>, <code>numberOfBufferConsumers</code>
     * and <code>numberOfPersistedQueueConsumers</code>.
     * 
     * @see #startNewMission
     */
    public <T> void startNewMission(String missionName, String queueName,
            Factory<DirtyWork<T>> dirtyWorkFactory,
            Capsule<T> capsule) {

        startNewMission(missionName, queueName, dirtyWorkFactory,
                capsule, Mission.DEFAULT_VOLATILE_BUFFER_SIZE,
                Mission.SMART_NUMBER_OF_CONSUMERS,
                Mission.SMART_NUMBER_OF_CONSUMERS);
    }

    /**
     * Stop all missions.
     */
    public void finalize() throws Throwable {
        for (String mission : this.missions.keySet()) {
            this.stopTheMission(mission);
        }

        super.finalize();
    }

    /**
     * Stop a specific mission.
     *
     * @param missionName The mission name used when you have started a mission.
     * @throws InterruptedException Because it will wait for every consumer die.
     * @throws UnregisteredMissionException If the mission was not started before.
     */
    public void stopTheMission(String missionName) throws
            InterruptedException, UnregisteredMissionException {

        if (!this.missions.containsKey(missionName)) {
            throw new UnregisteredMissionException(missionName);
        } else {
            this.missions.get(missionName).stopTheMission();
        }
    }

    /**
     * Just to keep the factory contract, use {@link #getANewMachineGun}.
     * This function will use the last used mission.
     * 
     * @see #getANewMachineGun
     */
    @Override
    public MachineGun buildANewInstance() {
        return this.getANewMachineGun(lastUsedMission);
    }

    public Mission getAMission(String missionName) {
        if (!missions.containsKey(missionName)) {
            throw new UnregisteredMissionException("The mission '"
                    + missionName
                    + "' was not registered yet! See 'startNewMission(...)'"
                    + " function.");
        }

        return missions.get(missionName);
    }

    /**
     * Produces a shiny and new machine gun.
     *
     * @throws UnregisteredMissionException If you forget to {@link #startNewMission start a mission}
     *                                      before using this.
     *
     * @param missionName the associated mission
     * @return a new machine gun to be used immediately.
     */
    public <T> MachineGun<T> getANewMachineGun(final String missionName) throws UnregisteredMissionException {
        lastUsedMission = missionName;

        @SuppressWarnings("unchecked")
        final Mission<T> mission = (Mission<T>) getAMission(missionName);

        return new MachineGun<T>() {
            @Override
            public void fire(T bullet) throws InterruptedException {
                mission.fire(bullet);
            }
        };
    }
}
