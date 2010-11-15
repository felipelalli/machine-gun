package br.fml.eti.machinegun;

/**
 * An expensive task.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:27:08 AM
 */
public interface HeavyTask<T> {
    /**
     * It can take a loooong time!
     */
    public void process(T dataToBeProcessed);
}
