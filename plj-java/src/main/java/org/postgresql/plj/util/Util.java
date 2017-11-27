package org.postgresql.plj.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class Util {

  public static boolean readString(StringBuffer stringBuffer, ByteBuf byteBuf){

    if (byteBuf.readableBytes() >= 4 ) {
      int strlen = byteBuf.readIntLE();
      // TODO string is really null not empty fix
      if (strlen < 0) return true;
      if (byteBuf.readableBytes() >= strlen ) {
        stringBuffer.append(byteBuf.readBytes(strlen).toString(Charset.defaultCharset()));
        return true;
      }
    }
    return false;
  }

}
