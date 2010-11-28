/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.behavior;

/**
 * Produces something.
 * <pre>
 *         ,
                   _cc,
                  '.())(((c
                   (((((()(c
                  '(((('_ _'
                  `(((    \
                   (`\   -
                   '  )  ( __
                  _.-'    '  `.
                 /    ,   -'   \
                 L     `        .
                 \ /       `    |
                 `c   )  c  )   J
                 .\__. `---':   |
                 \__  \,    |   L
                  /`\\\>    L  |
                 /         /  J
                 L c      '  /
                 \       /  /\
                  \-.__.' .'  .
                  |`-___.'
a:f               :  `8P      /
                  :  .'      /
                  ` /      .'
                   /     .'
                  .    .'
                  L   '
                  \`
 </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 6:16:30 AM
 */
public abstract class Factory<T> {
    /**
     * Produces a new instance. Despite the name is "build a new instance",
     * the implementation can just return a cache or a singleton.
     * 
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
