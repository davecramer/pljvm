package org.postgresql.plj.arg;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;


public class StringArgument extends Argument {


  public StringArgument(String name, String value) {
    super(name);
    clazz = String.class;
    this.value = value;
  }
  public static  StringArgument getInstance(String name, ByteBuf byteBuf)
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
