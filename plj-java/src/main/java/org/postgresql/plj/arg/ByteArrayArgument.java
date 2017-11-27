package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;

public class ByteArrayArgument extends Argument {


    public ByteArrayArgument(StringBuffer name, int length) {
        super(name);
        this.clazz = byte[].class;
        this.value = new byte[length];
    }
    public static  ByteArrayArgument getInstance(StringBuffer name, ByteBuf byteBuf)
    {
        if (byteBuf.readableBytes()>=1) {
            if ( byteBuf.readByte() == 'D' ) {
                int arrayLength = byteBuf.readIntLE();
                if (byteBuf.readableBytes() >= arrayLength) {
                    ByteArrayArgument byteArrayArgument = new ByteArrayArgument(name, arrayLength);
                    // getBytes looks at the length of the destination to figure out how many bytes
                    byteBuf.getBytes(byteBuf.readerIndex(), (byte [])byteArrayArgument.value);
                    //getBytes does not move the index so we have to
                    byteBuf.readerIndex(byteBuf.readerIndex()+ arrayLength);
                    return byteArrayArgument;
                }
            } else {
                return new ByteArrayArgument(name, (short)0);
            }
        }
        return null;
    }
}
