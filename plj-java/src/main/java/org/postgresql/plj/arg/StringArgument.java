package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;


public class StringArgument extends Argument {


  public StringArgument(StringBuffer name, String value) {
    clazz = String.class;
    this.name = name.length()==0?null:name.toString();
    this.value = value;
  }
  public static  StringArgument getInstance(StringBuffer name, ByteBuf byteBuf)
  {
    if (byteBuf.readableBytes()>=1) {
      byte isNull = byteBuf.readByte();
      if (isNull == 'D') {
        if (byteBuf.readableBytes() >= 4) {
          int strlen = byteBuf.readIntLE();
          if (byteBuf.readableBytes() >= strlen) {
            return new StringArgument(name, byteBuf.readBytes(strlen).toString(Charset.defaultCharset()));
          }
        }
      } else {
        return new StringArgument(name, null);
      }
    }
    return null;
  }

}
