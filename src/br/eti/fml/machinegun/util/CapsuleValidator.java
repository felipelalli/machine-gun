/*
 * Copyright (c) 2010.
 * CC-by Felipe Micaroni Lalli
 */

package br.eti.fml.machinegun.util;

import br.eti.fml.machinegun.Capsule;
import br.eti.fml.machinegun.WrongCapsuleException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test your {@link br.eti.fml.machinegun.Capsule} implementation here.
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 20, 2010 10:41:18 PM
 */
public class CapsuleValidator<T> {
    private Capsule<T> capsule;
    private T valueToTest;

    public CapsuleValidator(Capsule<T> capsule, T valueToTest) {
        this.capsule = capsule;
        this.valueToTest = valueToTest;
    }

    @Test
    public void test() throws WrongCapsuleException {
        byte[] bytes = capsule.convertToBytes(valueToTest);
        T restored = capsule.restoreFromBytes(bytes);
        Assert.assertEquals(valueToTest, restored);
    }
}
