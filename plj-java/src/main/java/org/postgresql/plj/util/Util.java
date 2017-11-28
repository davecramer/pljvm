package org.postgresql.plj.util;

import org.postgresql.plj.arg.Argument;

import com.sun.org.apache.xpath.internal.Arg;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Method;
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

  public static Object getInstanceOf(String className, Argument [] components) throws Exception
  {
    Class  clazz = Class.forName(className);

    Object obj = clazz.newInstance();

    for (Argument arg:components){
      String setter = "set";
      byte []setterName = arg.getName().getBytes();
      setterName[0] = (byte)Character.toUpperCase(setterName[0]);
      setter = setter + new String(setterName);
      Method method = clazz.getMethod(setter, arg.getClazz());
      method.invoke(obj, arg.getValue());
    }
    return obj;



  }
}
