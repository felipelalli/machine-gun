/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun;

import br.eti.fml.behavior.BuildingException;
import br.eti.fml.machinegun.auditorship.ArmyAudit;
import br.eti.fml.machinegun.externaltools.Consumer;
import br.eti.fml.machinegun.externaltools.ImportedWeapons;
import br.eti.fml.machinegun.externaltools.PersistedQueueManager;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * To start a new mission, see {@link br.eti.fml.machinegun.Army#startNewMission}.
 * </p>
 * <p>
 * A {@link Mission} knows his {@link Target} and keep
 * working with some thread consumers to make the "bullet" (data)
 * reach its specified destination. A captain of a {@link Mission}
 * is good only with a kind of bullet, because it you can say the
 * mission "is of a specific type".
 * </p>
 * <p>
 * In a less abstract, a {@link Mission} is a group of {@link Thread threads}
 * consuming the <i>first internal buffer</i> to put the bullets
 * (data) into a persisted embedded queue. Also, there are
 * other {@link Thread threads} consuming from this queue to execute the
 * {@link DirtyWork dirty work} associated with the kind of bullet (data).
 * </p>
 * <p>
 * A Mission will wait (idle) until
 * the method {@link #startTheMission()} is called.
 * </p>
 * <pre>
  _
 (_)
<___>
 | |_______
 | |`-._`-.)
 | |`-._`-(__________
 | |    `-.| :|    |:)
 | | _ _ _ | :|    |(__________
 | |-------| :|    |:| _.-'_.-'|
 | |       |`:|    |:|'_.-'_.-'|
 | |_______|--      -|'_.-'    |
 | |- - - -|         |' _ _ _ _|
 | |     _.|__      _|---------|
 | | _.-'_.|-:|    |:|         |
 | |'_.-'_.|':|    |:|_________|
 | |~~~~~~~| :|    |:|- - - - -|
 | |       | :|    |:|`-._     |
 | |       '~~~~~~~~~|`-._`-._ |
 | |                 |`-._`-._`|
 | |                 '~~~~~~~~~~
 | |
</pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 2:18:28 PM
 */
public class Mission<BulletType> {
    // monitoring and external tools
    private ArmyAudit armyAudit;
    private PersistedQueueManager persistedQueueManager;

    // target
    private Target<BulletType> target;
    private Capsule<BulletType> capsule;

    // consumers
    private BlockingQueue<byte[]> buffer;
    private Thread[] bufferConsumers;
    private int numberOfPersistedQueueConsumers;
    private int volatileBufferSize;

    // aux
    private Random random = new Random();
    private boolean end = false;

    /**
     * The default is {@value}. Use 1 if you can't lose any data. Use
     * big values if you have a lot of RAM memory and you don't care
     * so much if you lost something on system crashes.
     */
    public static final int DEFAULT_VOLATILE_BUFFER_SIZE = 1;

    /**
     * It will use {@link Runtime#availableProcessors()}
     * for number of the internal buffer consumers and
     * {@link Runtime#availableProcessors()}<code> * 5</code>
     * for the persisted queue consumers.
     */
    public static final int SMART_NUMBER_OF_CONSUMERS = 0;    

    /**
     * See the {@link Army#startNewMission} documentation.
     * 
     * @param armyAudit to monitoring
     * @param importedWeapons external implementations
     * @param target destination (queue name)
     * @param capsule a way to convert a bullet (data) into an array bytes and vice-versa
     * @param volatileBufferSize use 1 if you want to NEVER LOSE any data
     * @param numberOfBufferConsumers more if your PC has more memory and processors
     * @param numberOfPersistedQueueConsumers more if your PC has more memory and processors
     */
    public Mission(ArmyAudit armyAudit, ImportedWeapons importedWeapons,
                   Target<BulletType> target, Capsule<BulletType> capsule,
                   int volatileBufferSize, int numberOfBufferConsumers,
                   int numberOfPersistedQueueConsumers) {

        if (volatileBufferSize < 1) {
            throw new IllegalArgumentException("buffer size must be >= 1");
        }

        if (numberOfBufferConsumers < 1) {
            numberOfBufferConsumers
                    = Runtime.getRuntime().availableProcessors() * 2;

            if (numberOfBufferConsumers < 1) {
                 numberOfBufferConsumers = 1;
            }
        }

        if (volatileBufferSize == 1) {
            numberOfBufferConsumers = 0;
        }

        if (numberOfPersistedQueueConsumers < 1) {
            numberOfPersistedQueueConsumers
                    = Runtime.getRuntime().availableProcessors() * 4;

            if (numberOfPersistedQueueConsumers < 1) {
                numberOfPersistedQueueConsumers = 1;
            }
        }

        this.target = target;
        this.capsule = capsule;
        this.armyAudit = armyAudit;
        this.persistedQueueManager = importedWeapons.getQueueManager();

        this.volatileBufferSize = volatileBufferSize;
        this.buffer = new ArrayBlockingQueue<byte[]>(volatileBufferSize);
        this.bufferConsumers = new Thread[numberOfBufferConsumers];
        this.numberOfPersistedQueueConsumers = numberOfPersistedQueueConsumers;
    }

    /**
     * If the internal buffer is busy, this function
     * will block indefinitely. But because it uses a asynchronous embedded
     * queue, it should be very fast and non block function in most of cases.
     *
     * @throws InterruptedException Because this function can block.
     * @param bullet The data to reach the target.
     */
    public void fire(BulletType bullet) throws InterruptedException {
        try {
            byte[] data = this.capsule.convertToBytes(bullet);

            if (this.volatileBufferSize == 1) {
                internalBufferConsumerDoWork(data);
            } else {
                this.buffer.put(data);
            }

            armyAudit.updateCurrentBufferSize(buffer.size(), volatileBufferSize);
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnDataSerialization(e);
        }
    }

    /**
     * Starts the internal buffer consumers threads and registers the
     * persisted embedded queue consumers.
     */
    public void startTheMission() {
        if (this.volatileBufferSize > 1) { // if it is 1, the only buffer
                                           // consumer will work immediately

            for (int i = 0; i < this.bufferConsumers.length; i++) {
                String threadName = "Internal buffer consumer "
                        + (i+1) + " of " + volatileBufferSize;

                Thread internalBufferConsumer = new Thread(threadName) {
                    public void run() {
                        while (!end || buffer.size() > 0) {
                            try {
                                byte[] data = buffer.poll(5, TimeUnit.SECONDS);

                                if (data != null) {
                                    internalBufferConsumerDoWork(data);
                                }
                            } catch (InterruptedException e) {
                                end = true;
                            }
                        }
                    }
                };

                this.bufferConsumers[i] = internalBufferConsumer;
                internalBufferConsumer.start();
            }
        }

        for (int i = 0; i < this.numberOfPersistedQueueConsumers; i++) {
            final String consumer = "Consumer " + (i+1) + " of "
                    + this.numberOfPersistedQueueConsumers;

            registerAConsumerInEmbeddedQueue(
                    target.getQueueName(),
                    new Consumer() {
                        @Override
                        public void consume(byte[] crudeData) {
                            consumerWork(consumer, crudeData);
                        }
                    });

            armyAudit.consumerIsReady(consumer);
        }
    }

    private void consumerWork(String consumerName, byte[] crudeData) {
        try {
            BulletType data = capsule.restoreFromBytes(crudeData);
            long id = random.nextLong();

            try {
                armyAudit.aConsumerStartsHisJob(id, consumerName);
                target.workOnIt(id, consumerName, armyAudit, data);
                // workOnIt MUST call {@link ArmyAudit#aConsumerHasBeenFinishedHisJob}
            } catch (BuildingException e) {
                armyAudit.aConsumerHasBeenFinishedHisJob(
                        id, consumerName, false, e,
                        consumerName + ": dirtyWorkFactory didn't work fine: "
                        + e);
            }
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnDataSerialization(e);
        }
    }

    private void internalBufferConsumerDoWork(byte[] data)
            throws InterruptedException {

        armyAudit.updateCurrentBufferSize(buffer.size(), volatileBufferSize);
        putDataIntoAnEmbeddedQueue(target.getQueueName(), data);
    }

    /**
     * Kill all consumers (first of buffers and after persisted queue).
     * @throws InterruptedException Because it waits the threads die.
     */
    public void stopTheMission() throws InterruptedException {
        if (!end) {
            end = true;

            for (Thread t : bufferConsumers) {
                if (t != null) {
                    t.join();
                }
            }

            persistedQueueManager.killAllConsumers(target.getQueueName());
        }
    }

    private void putDataIntoAnEmbeddedQueue(String queueName, byte[] data)
            throws InterruptedException {
        
        persistedQueueManager.putIntoAnEmbeddedQueue(armyAudit, queueName, data);
    }

    private void registerAConsumerInEmbeddedQueue(
            String queueName, Consumer consumer) {

        persistedQueueManager.registerANewConsumerInAnEmbeddedQueue(
                armyAudit, queueName, consumer);
    }    
}
