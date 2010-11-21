package br.fml.eti.machinegun;

/**
 * <p>
 * A "machine gun" avoid stopping the main flow when
 * {@link br.fml.eti.machinegun.DirtyWork expensive things} is needed,
 * like I/O operations. First, a {@link MachineGun} will put everything in an (limited)
 * internal buffer, after some consumers will put it in an internal persisted
 * queue to finally, others consumers to do the expensive operation. A "machine gun"
 * is very useful, e.g., when you need a fast (and asynchronous) persistence into
 * a (slow) database. 
 * </p>
 * <p>
 * Everything can be externally monitored through a {@link
 * br.fml.eti.machinegun.auditorship.ArmyAudit}.
 * </p>
<pre>
      .
      ,,                        __,--.
     ''      ,                 /_,__,_)        ,----.
     '      /|________________//__   __________|'##'|_____
      =====(  )_##################)==`)###################|
           ,' |_)#################)===)#######__`__`__`___|____
          =`--"._,_,_#############)===)######(_,_,_,_,_,_,_,_,_)
                     '--,_,__,__|.------.,--.,-.[JW],-----'
                                 |-##-| ||  )) |###(
                                 |-##-| '======|,###\
                                 '----'        \,####\
                                                \,####)
                                                 \_,-"
     </pre>
 *
 * @see Army
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 5:33:07 AM
 */
public abstract class MachineGun<T> {
    /**
     * <p>
     * You can use a {@link MachineGun} to "fire" the <code>data</code>
     * as fast as possible into an internal buffer to, after, be placed
     * in an persisted embedded queue to be processed as soon as possible
     * until finally reach the final {@link Target target}.
     *
     * @throws UnregisteredMissionException If you forget to start a mission
     *                                      before using {@link Army#startNewMission}.
     * @throws InterruptedException If the army is busy (the internal buffer is full)
     *                              so this function can block for a while.
     *
     * @param bullet The <b>data</b> to be processed.
     *
     * @param missionName Where and how the "bullet" (data) will reach the target.
     *               You <u>have to</u> register a mission in the {@link Army}:
     *               see the {@link Army#startNewMission} to know how to do this.
     */
    public abstract void fire(T bullet, String missionName)
            throws UnregisteredMissionException, InterruptedException;
}
