package br.fml.eti.machinegun;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.Consumer;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A {@link Mission} knows his {@link Target} and keep
 * working with his soldiers (thread consumers) to make the "bullet" (data)
 * reach your right destination. A captain of a {@link Mission}
 * is good only with a kind of bullet.
 * </p>
 * <p>
 * In a less abstract, a {@link Mission} is a group of {@link Thread threads}
 * (front line soldiers) consuming the first internal buffer to put the data
 * (bullets) into a embedded queue. Also, there are some {@link Thread threads}
 * (rear soldiers) consuming from this queue to execute the
 * {@link DirtyWork dirty work} associated with the kind of bullet (data).
 * </p>
 * <p>
 * A Mission will wait until the method {@link #startTheMission()} is called.
 * </p>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 2:18:28 PM
 */
public class Mission<BulletType> {
    // monitoring and external tools
    private ArmyAudit armyAudit;
    private ImportedWeapons importedWeapons;

    // target
    private Target<BulletType> target;
    private Capsule<BulletType> capsule;

    // consumers
    private BlockingQueue<byte[]> buffer;
    private Thread[] frontLineSoldiers;
    private int rearNumberOfSoldiers;
    private int bufferSize;

    // aux
    private Random random = new Random();
    private boolean end = false;

    public Mission(ArmyAudit armyAudit, ImportedWeapons importedWeapons,
                   Target<BulletType> target, Capsule<BulletType> capsule,
                   int bufferSize, int frontLineNumberOfSoldiers,
                   int rearNumberOfSoldiers) {

        if (bufferSize < 1) {
            throw new IllegalArgumentException("buffer size (buffer size) must be one or more.");
        }

        if (frontLineNumberOfSoldiers < 1) {
            frontLineNumberOfSoldiers
                    = Runtime.getRuntime().availableProcessors() * 2;

            if (frontLineNumberOfSoldiers < 1) {
                frontLineNumberOfSoldiers = 1;
            }
        }

        if (rearNumberOfSoldiers < 1) {
            rearNumberOfSoldiers
                    = Runtime.getRuntime().availableProcessors() * 5;

            if (rearNumberOfSoldiers < 1) {
                rearNumberOfSoldiers = 1;
            }
        }

        this.target = target;
        this.capsule = capsule;
        this.armyAudit = armyAudit;
        this.importedWeapons = importedWeapons;

        this.bufferSize = bufferSize;
        this.buffer = new LinkedBlockingQueue<byte[]>(bufferSize);
        this.frontLineSoldiers = new Thread[frontLineNumberOfSoldiers];
        this.rearNumberOfSoldiers = rearNumberOfSoldiers;
    }

    /**
     * If the buffer (internal buffer) is busy, this function
     * will block indefinitely. But because it uses a asynchronous embedded
     * queue, it should be very fast and non block function.
     *
     * @throws InterruptedException Because this function can block.
     * @param bullet The data to reach the target.
     */
    public void fire(BulletType bullet) throws InterruptedException {
        try {
            byte[] data = this.capsule.convertToBytes(bullet);

            if (this.bufferSize == 1) {
                internalBufferConsumerDoWork(data);
            } else {
                this.buffer.put(data);
            }

            armyAudit.updatePreBufferCurrentSize(buffer.size(), bufferSize);
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnDataSerialization(e);
        }
    }

    public void startTheMission() {
        if (this.bufferSize > 1) { // if it is 1, the only soldier will work immediately
            for (int i = 0; i < this.frontLineSoldiers.length; i++) {
                String threadName = "Internal buffer consumer "
                        + (i+1) + " of " + bufferSize;

                Thread soldier = new Thread(threadName) {
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

                this.frontLineSoldiers[i] = soldier;
                soldier.start();
            }
        }

        for (int i = 0; i < this.rearNumberOfSoldiers; i++) {
            final String soldier = "Rear soldier " + i;

            registerAConsumerInEmbeddedQueue(
                    target.getQueueName(),
                    new Consumer() {
                        @Override
                        public void consume(byte[] crudeData) {
                            rearSoldierWork(soldier, crudeData);
                        }
                    });

            armyAudit.consumerIsReady(soldier);
        }
    }

    private void rearSoldierWork(String soldier, byte[] crudeData) {
        try {
            BulletType data = capsule.restoreFromBytes(crudeData);
            long id = random.nextLong();

            try {
                armyAudit.aConsumerStartsHisJob(id, soldier);
                target.workOnIt(id, soldier, armyAudit, data);
                // workOnIt MUST call ArmyAudit#aConsumerHasBeenFinishedHisJob
            } catch (BuildingException e) {
                armyAudit.aConsumerHasBeenFinishedHisJob(
                        id, soldier, false, e,
                        soldier
                        + ": dirtyWorkFactory didn't work fine: "
                        + e);
            }
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnDataSerialization(e);
        }
    }

    private void internalBufferConsumerDoWork(byte[] data)
            throws InterruptedException {

        armyAudit.updatePreBufferCurrentSize(buffer.size(), bufferSize);
        putInAEmbeddedQueue(target.getQueueName(), data);
    }

    /**
     * Kill all front line soldiers.
     * @throws InterruptedException Because it waits the threads die.
     */
    public void stopTheMission() throws InterruptedException {
        if (!end) {
            end = true;

            for (Thread t : frontLineSoldiers) {
                if (t != null) {
                    t.join();
                }
            }

            importedWeapons.getQueueManager().killAllConsumers(target.getQueueName());
        }
    }

    private void putInAEmbeddedQueue(String queueName, byte[] data)
            throws InterruptedException {
        
        importedWeapons.getQueueManager()
                .putInAEmbeddedQueue(armyAudit, queueName, data);
    }

    private void registerAConsumerInEmbeddedQueue(
            String queueName, Consumer consumer) {

        importedWeapons.getQueueManager()
                .registerAConsumerInEmbeddedQueue(armyAudit, queueName, consumer);
    }    
}
