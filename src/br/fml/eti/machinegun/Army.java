package br.fml.eti.machinegun;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.behavior.Factory;

import java.util.HashMap;
import java.util.Map;

/**
 * Produces {@link MachineGun machine guns}.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:15:37 AM
 */
public class Army extends Factory<MachineGun> {
    private int internalBufferSize;
    private int frontLineNumberOfConsumers;
    private int rearNumberOfConsumers;
    private Map<String, Factory<HeavyTask>> targets;

    /**
     * The default is {@value}.
     */
    public static final int DEFAULT_BUFFER_SIZE = 256;

    /**
     * It will use {@link Runtime#availableProcessors()}.
     */
    public static final int DYNAMIC_NUMBER_OF_CONSUMERS = 0;

    /**
     * Create a new Army.
     * 
     * @param internalBufferSize If the buffer is full the TODO
     *                           function will be blocked until the consumers
     *                           can drain the volume. You can use
     *                           {@link #DEFAULT_BUFFER_SIZE}. Set
     *                           high values if you have high available memory
     *                           and don't care so much about lost some data.
     *                           Remember that what is on the buffer will not be
     *                           persisted. If is important to persist ALL,
     *                           set this parameter to 1.
     *
     * @param frontLineNumberOfConsumers The number of consumers to read from
     *                                   internal buffer and put on internal
     *                                   queue. Use {@link #DYNAMIC_NUMBER_OF_CONSUMERS}
     *                                   to make the MachineGun calculates based
     *                                   on your {@link Runtime#availableProcessors()
     *                                   available processors}.
     *
     * @param rearNumberOfConsumers The number of embedded queue consumers.
     *                              This consumers will do the dirty and hard work.
     *                              Use {@link #DYNAMIC_NUMBER_OF_CONSUMERS}
     *                              to make the MachineGun calculates based
     *                              on your {@link Runtime#availableProcessors()
     *                              available processors}.
     */
    public Army(int internalBufferSize, int frontLineNumberOfConsumers,
                int rearNumberOfConsumers) {

        this.targets = new HashMap<String, Factory<HeavyTask>>();

        if (internalBufferSize < 1) {
            new IllegalArgumentException("internalBufferSize must be one or more.");
        }

        if (frontLineNumberOfConsumers < 1) {
            frontLineNumberOfConsumers = Runtime.getRuntime().availableProcessors();
        }

        if (rearNumberOfConsumers < 1) {
            rearNumberOfConsumers = Runtime.getRuntime().availableProcessors();
        }

        this.internalBufferSize = internalBufferSize;
        this.frontLineNumberOfConsumers = frontLineNumberOfConsumers;
        this.rearNumberOfConsumers = rearNumberOfConsumers;


    }

    /**
     * Associate a target (by the queue name) with a {@link HeavyTask heavy task}
     * {@link Factory factory}.
     *
     * @param targetAndQueueName The target and embedded queue name.
     * @param heavyTaskFactory the associated factory of heavy tasks.
     */
    public void registerTarget(String targetAndQueueName, Factory<HeavyTask> heavyTaskFactory) {
        this.targets.put(targetAndQueueName, heavyTaskFactory);
    }

    public HeavyTask getHeavyTaskByTargetName(String targetName) throws BuildingException {
        return this.targets.get(targetName).buildANewInstance();
    }

    @Override
    public MachineGun buildANewInstance() {
        
    }
}
