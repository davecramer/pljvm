package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class LongArgument extends Argument {

  public LongArgument(String name, Long value) {
    super(name);
    clazz = Long.class;
    this.value = value;
  }
  public static  LongArgument getInstance(String name, ByteBuf byteBuf)
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
