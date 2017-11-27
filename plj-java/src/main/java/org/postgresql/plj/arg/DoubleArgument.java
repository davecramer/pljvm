package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class DoubleArgument extends Argument {


  public DoubleArgument(StringBuffer name, Double value) {
    clazz = Double.class;
    this.name = name.length()==0?null:name.toString();
    this.value = value;
  }
  public static  DoubleArgument getInstance(StringBuffer name, ByteBuf byteBuf)
  {

    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 8) {
          return new DoubleArgument(name, byteBuf.readDoubleLE());
        }
      } else {
        return new DoubleArgument(name, null);
      }
    }
    return null;
  }

}
