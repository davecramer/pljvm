package org.postgresql.plj.arg;

import org.postgresql.plj.DataType;
import org.postgresql.plj.Type;
import org.postgresql.plj.util.Util;

import io.netty.buffer.ByteBuf;

public abstract class Argument {
  Object value;
  String name;
  Class clazz;
  DataType dataType;

  public Argument(String name){
    if ( name != null) {
      this.name = name.length() == 0 ? null : name;
    }else {
      this.name = null;
    }
  }

  public static Argument readArgument(ByteBuf byteBuf)
  {

    StringBuffer name = new StringBuffer();

    if (!Util.readString(name, byteBuf)){
      return null;
    }

    Type type = Type.getInstance(byteBuf);

    if ( type == null ) {
      return null;
    }

    return readArgument(type, name.toString(), byteBuf);

  }
  public static Argument readArgument (Type type, String name,  ByteBuf byteBuf)
  {
    Argument retVal = null;

    switch (type.getDataType()) {
      case PLJVM_DATA_INT1:
        retVal = BoolArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_INT2:
        retVal = Int16Argument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_INT4:
        retVal = Int32Argument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_INT8:
        retVal = LongArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_FLOAT4:
        retVal = FloatArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_FLOAT8:
        retVal = DoubleArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_TEXT:
        retVal = StringArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_BYTEA:
        retVal = ByteArrayArgument.getInstance(name, byteBuf);
        break;
      case PLJVM_DATA_ARRAY:
        retVal = ArrayArgument.getInstance(name, type.getSubTypes()[0], byteBuf);
        break;
      case PLJVM_DATA_UDT:
        retVal = UDTArgument.getInstance(name, type, byteBuf);
    }
    retVal.dataType = type.getDataType();

    return retVal;
  }
  public Object getValue(){
    return value;
  }
  public Class getClazz() {
    return clazz;
  }
  public String getName() { return name;}
  public DataType getDataType() { return dataType; }
}
