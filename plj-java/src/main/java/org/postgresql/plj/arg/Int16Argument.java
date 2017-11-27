package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;


public class Int16Argument extends Argument {

  public Int16Argument(StringBuffer name, Short value) {
    super(name);
    clazz = Short.class;
    this.value = value;
  }
  public static  Int16Argument getInstance(StringBuffer name, ByteBuf byteBuf)
  {
    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 2) {
          return new Int16Argument(name, byteBuf.readShortLE());
        }
      } else {
        return new Int16Argument(name, null);
      }
    }
    return null;
  }

}
