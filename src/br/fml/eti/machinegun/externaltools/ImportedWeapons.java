/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun.externaltools;

/**
 * <p>A class just to group external tools (specific implementations),
 * e.g. persisted queues specific implementations.</p>
 * <pre>
                                      ____    _     __     _    ____
                                     |####`--|#|---|##|---|#|--'##|#|
   _                                 |____,--|#|---|##|---|#|--.__|_|
 _|#)_____________________________________,--'EEEEEEEEEEEEEE'_=-.
((_____((_________________________,--------[JW](___(____(____(_==)        _________
                               .--|##,----o  o  o  o  o  o  o__|/`---,-,-'=========`=+==.
                               |##|_Y__,__.-._,__,  __,-.___/ J \ .----.#############|##|
                               |##|              `-.|#|##|#|`===l##\   _\############|##|
                              =======-===l          |_|__|_|     \##`-"__,=======.###|##|
                                                                  \__,"          '======'  
 * </pre>
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:35:40 PM
 */
public class ImportedWeapons {
    private PersistedQueueManager persistedQueueManager;

    public ImportedWeapons(PersistedQueueManager persistedQueueManager) {
        this.persistedQueueManager = persistedQueueManager;
    }

    public PersistedQueueManager getQueueManager() {
        return this.persistedQueueManager;
    }
}
