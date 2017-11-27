package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;
import org.postgresql.plj.Type;



public class UDTArgument extends Argument {


    public UDTArgument(StringBuffer name, Argument []value) {
        super(name);
        clazz = String.class;
        this.value = value;
    }
    public static  UDTArgument getInstance(StringBuffer name, Type type, ByteBuf byteBuf)
    {
        Argument args[];
        if (byteBuf.readableBytes()>=1) {
            byte isNull = byteBuf.readByte();
            if (isNull == 'D') {
                args = new Argument[type.getNumSubTypes()];
                for (int i=0; i<type.getNumSubTypes();i++){
                    args[i]=Argument.readArgument(type.getSubType(i), type.getSubType(i).getTypeName() , byteBuf);
                }
                return new UDTArgument(name, args);
            } else {
                return new UDTArgument(name, null);
            }
        }
        return null;
    }

}
