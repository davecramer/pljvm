package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class LongArgument extends Argument {

  public LongArgument(StringBuffer name, Long value) {
    clazz = Long.class;
    this.name = name.length()==0?null:name.toString();
    this.value = value;
  }
  public static  LongArgument getInstance(StringBuffer name, ByteBuf byteBuf)
  {
    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 8) {
          return new LongArgument(name, byteBuf.readLongLE());
        }
      } else {
        return new LongArgument(name, null);
      }
    }
    return null;
  }

}
