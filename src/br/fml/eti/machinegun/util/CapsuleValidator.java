package br.fml.eti.machinegun.util;

import br.fml.eti.machinegun.Capsule;
import br.fml.eti.machinegun.WrongCapsuleException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test your {@link br.fml.eti.machinegun.Capsule} implementation here.
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
