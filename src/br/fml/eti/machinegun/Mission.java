package br.fml.eti.machinegun;

import br.fml.eti.behavior.BuildingException;
import br.fml.eti.machinegun.auditorship.ArmyAudit;
import br.fml.eti.machinegun.externaltools.Consumer;
import br.fml.eti.machinegun.externaltools.ImportedWeapons;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private int battalionSize;

    // aux
    private Random random = new Random();

    public Mission(ArmyAudit armyAudit, ImportedWeapons importedWeapons,
                   Target<BulletType> target, Capsule<BulletType> capsule,
                   int battalionSize, int frontLineNumberOfSoldiers,
                   int rearNumberOfSoldiers) {

        if (battalionSize < 1) {
            throw new IllegalArgumentException("battalion size (buffer size) must be one or more.");
        }

        if (frontLineNumberOfSoldiers < 1) {
            frontLineNumberOfSoldiers
                    = Runtime.getRuntime().availableProcessors();

            if (frontLineNumberOfSoldiers < 1) {
                frontLineNumberOfSoldiers = 1;
            }
        }

        if (rearNumberOfSoldiers < 1) {
            rearNumberOfSoldiers
                    = Runtime.getRuntime().availableProcessors() * 3;

            if (rearNumberOfSoldiers < 1) {
                rearNumberOfSoldiers = 1;
            }
        }

        this.target = target;
        this.capsule = capsule;
        this.armyAudit = armyAudit;
        this.importedWeapons = importedWeapons;

        this.battalionSize = battalionSize;
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
        try {
            byte[] data = this.capsule.convertToBytes(bullet);

            if (this.battalionSize == 1) {
                frontLineSoldierWork("forever alone soldier", data);
            } else {
                this.battalion.put(data);
            }

            armyAudit.updateBattalionSize(battalion.size(), battalionSize);
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnBulletCapsule(e);
        }
    }

    public void startTheMission() {
        if (this.battalionSize > 1) { // if it is 1, the only soldier will work immediately
            for (int i = 0; i < this.frontLineSoldiers.length; i++) {
                final String soldierName = "Front line soldier " + i;
                Thread soldier = new Thread(soldierName) {
                    public void run() {
                        boolean interrupted = false;
                        armyAudit.frontLineSoldierIsReady(soldierName);

                        while (!interrupted) {
                            try {
                                byte[] data = battalion.take();
                                frontLineSoldierWork(soldierName, data);
                            } catch (InterruptedException e) {
                                interrupted = true;
                                armyAudit.frontLineSoldierDied(soldierName);
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

            armyAudit.rearSoldierIsReady(soldier);
        }
    }

    private void rearSoldierWork(String soldier, byte[] crudeData) {
        try {
            BulletType data = capsule.restoreFromBytes(crudeData);
            long id = random.nextLong();

            try {
                armyAudit.rearSoldierStartsHisJob(id, soldier);
                target.workOnIt(id, soldier, armyAudit, data);
                // workOnIt MUST call ArmyAudit#rearSoldierFinishesHisJob
            } catch (BuildingException e) {
                armyAudit.rearSoldierFinishesHisJob(
                        id, soldier, false, e,
                        soldier
                        + ": dirtyTaskFactory didn't work fine: "
                        + e);
            }
        } catch (WrongCapsuleException e) {
            armyAudit.errorOnBulletCapsule(e);
        }
    }

    private void frontLineSoldierWork(String soldierName, byte[] data)
            throws InterruptedException {
        
        long id = random.nextLong();
        armyAudit.updateBattalionSize(battalion.size(), battalionSize);
        armyAudit.frontLineSoldierStartsHisJob(id, soldierName);
        putInAEmbeddedQueue(target.getQueueName(), data);
        armyAudit.frontLineSoldierFinishesHisJob(id, soldierName);
    }

    /**
     * Kill all front line soldiers.
     * @throws InterruptedException Because it waits the threads die.
     */
    public void stopTheMission() throws InterruptedException {
        for (Thread t : frontLineSoldiers) {
            if (t != null) {
                t.interrupt();
            }
        }

        for (Thread t : frontLineSoldiers) {
            if (t != null) {
                t.join();
            }
        }

        importedWeapons.getQueueManager().killAllConsumers(target.getQueueName());
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
