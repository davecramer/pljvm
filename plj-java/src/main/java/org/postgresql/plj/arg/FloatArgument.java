package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class FloatArgument extends Argument{


  public FloatArgument(String name, Float value) {
    super(name);
    clazz = Float.class;
    this.value = value;
  }

  public static  FloatArgument getInstance(String name, ByteBuf byteBuf)
  {

    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 4) {
          return new FloatArgument(name, byteBuf.readFloatLE());
        }
      } else {
        return new FloatArgument(name, null);
      }
    }
    return null;
  }

}
