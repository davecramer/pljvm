package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;

public class Int32Argument extends Argument {


  public Int32Argument(StringBuffer name, Integer value) {
    clazz = Integer.class;
    this.name = name.length()==0?null:name.toString();
    this.value = value;
  }
  public static  Int32Argument getInstance(StringBuffer name, ByteBuf byteBuf)
  {
    if (byteBuf.readableBytes()>=1) {
      if ( byteBuf.readByte() == 'D' ) {
        if (byteBuf.readableBytes() >= 4) {
          return new Int32Argument(name, byteBuf.readIntLE());
        }
      } else {
        return new Int32Argument(name, null);
      }
    }
    return null;
  }

}
