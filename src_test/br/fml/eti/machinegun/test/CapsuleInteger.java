package br.fml.eti.machinegun.test;

import br.fml.eti.machinegun.Capsule;
import br.fml.eti.machinegun.WrongCapsuleException;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 *
 * @author Felipe Micaroni Lalli (micaroni@gmail.com)
 *         Nov 15, 2010 7:00:47 PM
 */
public class CapsuleInteger implements Capsule<Integer> {
    @Override
    public Integer restoreFromBytes(byte[] data) throws WrongCapsuleException {
        try {
            DataInteger.DataIntegerMessage dataInteger
                    = DataInteger.DataIntegerMessage.parseFrom(data);

            return dataInteger.getOnlyData();
        } catch (InvalidProtocolBufferException e) {
            throw new WrongCapsuleException(e); 
        }
    }

    @Override
    public byte[] convertToBytes(Integer data) throws WrongCapsuleException {
        DataInteger.DataIntegerMessage.Builder dataInteger
                = DataInteger.DataIntegerMessage.newBuilder();

        dataInteger.setOnlyData(data);
        DataInteger.DataIntegerMessage bytes = dataInteger.build();
        return bytes.toByteArray();
    }
}
