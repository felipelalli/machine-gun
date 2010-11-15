package br.fml.eti.machinegun.externaltools;

/**
 * Consumes and process a data.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 4:00:18 PM
 */
public interface Consumer {
    void consume(byte[] data);
}
