package br.fml.eti.behavior;

/**
 * Produces something.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:16:30 AM
 */
public abstract class Factory<T> {
    /**
     * Produces a new instance.
     * @return a new instance.
     */
    public abstract T buildANewInstance() throws BuildingException;

    /**
     * Produces N instances. This function can be optimized by specialized
     * classes.
     *
     * @param n number of needed instances.
     * @return an array with all instances required.
     */
    public T[] buildNewInstances(int n) throws BuildingException {
        T[] instances = (T[]) new Object[n];

        for (int i = 0; i < n; i++) {
            instances[i] = buildANewInstance();
        }

        return instances;
    }
}
