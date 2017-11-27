package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class FloatArgument extends Argument{


  public FloatArgument(StringBuffer name, Float value) {
    clazz = Float.class;
    this.name = name.length()==0?null:name.toString();

    this.value = value;
  }

  public static  FloatArgument getInstance(StringBuffer name, ByteBuf byteBuf)
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
