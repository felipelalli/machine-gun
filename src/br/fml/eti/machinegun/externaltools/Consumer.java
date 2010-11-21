/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.fml.eti.machinegun.externaltools;

/**
 * <p>
 * Consumes and processes data, simple like that.
 * </p>
 * <pre>
                 ___
               ."   ".
               |  ___(
               ).' -(
                )  _/
              .'_`(
             / ( ,/;
            /   \ ) \\.
           /'-./ \ '.\\)
           \   \  '---;\
           |`\  \      \\
          / / \  \      \\
        _/ /   / /      _\\/
       ( \/   /_/       \   |
    jgs \_)  (___)       '._/
 </pre>
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:00:18 PM
 */
public interface Consumer {
    void consume(byte[] data);
}
