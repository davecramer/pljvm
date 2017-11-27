package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class BoolArgument extends Argument {

  public BoolArgument(StringBuffer name, Boolean value) {
    super(name);
    clazz = boolean.class;
    this.value = value;

  }
  public static BoolArgument getInstance(StringBuffer name, ByteBuf byteBuf)
  {
    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 1) {
            return new BoolArgument(name, byteBuf.readBoolean());
        }
      } else {
        return new BoolArgument(name, null);
      }
    }
    return null;
  }

}
