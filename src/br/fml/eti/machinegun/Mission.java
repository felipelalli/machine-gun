package br.fml.eti.machinegun;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.Consumer;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 * A {@link Mission} knows his {@link Target} and keep
 * working with his soldiers (thread consumers) to make the "bullet" (data)
 * reach your right destination. A {@link Mission} is good only with a
 * kind of bullet.
 * </p>
 * <p>
 * In a less abstract, a {@link Mission} is a group of {@link Thread threads}
 * (front line soldiers) consuming the first internal buffer to put the data
 * (bullets) into a embedded queue. Also, there are some {@link Thread threads}
 * (rear soldiers) consuming from this queue to execute the
 * {@link DirtyTask dirty task} associated with the kind of bullet (data).
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
    private BlockingQueue<byte[]> battalion;
    private Thread[] frontLineSoldiers;
    private int rearNumberOfSoldiers;

    public Mission(ArmyAudit armyAudit, ImportedWeapons importedWeapons,
                   Target<BulletType> target, Capsule<BulletType> capsule,
                   int battalionSize, int frontLineNumberOfSoldiers,
                   int rearNumberOfSoldiers) {

        if (battalionSize < 1) {
            throw new IllegalArgumentException("battalion size (buffer size) must be one or more.");
        }

        if (frontLineNumberOfSoldiers < 1) {
            frontLineNumberOfSoldiers = Runtime.getRuntime().availableProcessors() - 1;

            if (frontLineNumberOfSoldiers < 1) {
                frontLineNumberOfSoldiers = 1;
            }
        }

        if (rearNumberOfSoldiers < 1) {
            rearNumberOfSoldiers = Runtime.getRuntime().availableProcessors() * 3;

            if (rearNumberOfSoldiers < 2) {
                rearNumberOfSoldiers = 2;
            }
        }

        this.target = target;
        this.capsule = capsule;

        this.battalion = new LinkedBlockingQueue<byte[]>(battalionSize);
        this.frontLineSoldiers = new Thread[frontLineNumberOfSoldiers];
        this.rearNumberOfSoldiers = rearNumberOfSoldiers;
    }

    /**
     * If the battalion (internal buffer) is busy, this function
     * will block indefinitely. But because it uses a asynchronous embedded
     * queue, it should be very fast and non block function.
     *
     * @throws InterruptedException Because this function can block.
     * @param bullet The data to reach the target.
     */
    public void fire(BulletType bullet) throws InterruptedException {
        this.battalion.put(this.capsule.convertToBytes(bullet));
    }

    public void startTheMission() {
        for (int i = 0; i < this.frontLineSoldiers.length; i++) {
            Thread soldier = new Thread("Front line soldier " + i) {
                public void run() {
                    boolean interrupted = false;

                    while (!interrupted) {
                        try {
                            byte[] data = battalion.take();
                            putInAEmbeddedQueue(target.getQueueName(), data);
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }
                }
            };

            this.frontLineSoldiers[i] = soldier;
            soldier.start();
        }

        for (int i = 0; i < this.rearNumberOfSoldiers; i++) {
            registerAConsumerInEmbeddedQueue(
                    target.getQueueName(),
                    new Consumer() {
                        @Override
                        public void consume(byte[] crudeData) {
                            BulletType data = capsule.restoreFromBytes(crudeData);

                            try {
                                target.workOnIt(data);
                            } catch (BuildingException e) {
                                // TODO monitoring
                            }
                        }
                    });
        }
    }

    public void stopTheMission() throws InterruptedException {
        for (Thread t : frontLineSoldiers) {
            t.interrupt();
        }

        for (Thread t : frontLineSoldiers) {
            t.join();
        }
    }

    private void putInAEmbeddedQueue(String queueName, byte[] data) {
        // TODO
    }

    private void registerAConsumerInEmbeddedQueue(
            String queueName, Consumer consumer) {
        
        // TODO
    }    
}
