package br.fml.eti.machinegun;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 * A {@link Captain} knows his {@link Target} and keep
 * working with his soldiers (thread consumers) to make the "bullet" (data)
 * reach your right destination. A {@link Captain} is good only with a
 * kind of bullet.
 * </p>
 * <p>
 * In a less abstract, a {@link Captain} is a group of {@link Thread threads}
 * (front line soldiers) consuming the first internal buffer to put the data
 * (bullets) into a embedded queue. Also, there are some {@link Thread threads}
 * (rear soldiers) consuming from this queue to execute the
 * {@link DirtyTask dirty task} associated with the kind of bullet (data).
 * </p>
 * <p>
 * A Captain will wait until the method {@link #startTheMission()} is called.
 * </p>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 2:18:28 PM
 */
public class Captain<BulletType> {
    private Target<BulletType> target;
    private Capsule<BulletType> capsule;

    private BlockingQueue<byte[]> battalion;
    private Thread[] frontLineSoldiers;
    private Thread[] rearSoldiers;

    public Captain(Target<BulletType> target, Capsule<BulletType> capsule,
                   int battalionSize, int frontLineNumberOfSoldiers,
                   int rearNumberOfSoldiers) {

        if (battalionSize < 1) {
            new IllegalArgumentException("battalion size (buffer size) must be one or more.");
        }

        if (frontLineNumberOfSoldiers < 1) {
            frontLineNumberOfSoldiers = Runtime.getRuntime().availableProcessors();
        }

        if (rearNumberOfSoldiers < 1) {
            rearNumberOfSoldiers = Runtime.getRuntime().availableProcessors();
        }

        this.target = target;
        this.capsule = capsule;

        this.battalion = new LinkedBlockingQueue<byte[]>(battalionSize);
        this.frontLineSoldiers = new Thread[frontLineNumberOfSoldiers];
        this.rearSoldiers = new Thread[rearNumberOfSoldiers];
    }

    public void startTheMission() {

    }
}
