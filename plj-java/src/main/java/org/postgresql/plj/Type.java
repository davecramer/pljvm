package org.postgresql.plj;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Array;
import java.nio.charset.Charset;


public class Type {
  Class clazz;
  DataType dataType;
  String typeName;
  short numSubtypes;
  Type []subTypes;

  public Type(DataType dataType)
  {
    this.dataType = dataType;
    switch (dataType){
      case PLJVM_DATA_INT1:
        typeName = "boolean";
        clazz = boolean.class;
        break;
      case PLJVM_DATA_INT2:
        typeName = "int2";
        clazz = short.class;
        break;
      case PLJVM_DATA_INT4:
        typeName = "int4";
        clazz = int.class;
        break;
      case PLJVM_DATA_INT8:
        typeName = "int8";
        clazz = long.class;
        break;
      case PLJVM_DATA_FLOAT4:
        typeName = "float4";
        clazz = float.class;
        break;
      case PLJVM_DATA_FLOAT8:
        typeName = "float8";
        clazz = double.class;
        break;
      case PLJVM_DATA_TEXT:
        typeName = "text";
        clazz = String.class;
        break;

    }
  }


  public static Type getInstance(ByteBuf byteBuf){
    if (!byteBuf.isReadable(1)) return null;
    byte dataype = byteBuf.readByte();
    Type type =null;
    if (dataype == DataType.PLJVM_DATA_INT1.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_INT1);
    }else if ( dataype == DataType.PLJVM_DATA_INT2.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_INT2);
    }else if ( dataype == DataType.PLJVM_DATA_INT4.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_INT4);
    }else if ( dataype == DataType.PLJVM_DATA_INT8.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_INT8);
    }else if ( dataype == DataType.PLJVM_DATA_FLOAT4.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_FLOAT4);
    }else if ( dataype == DataType.PLJVM_DATA_FLOAT8.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_FLOAT8);
    }else if ( dataype == DataType.PLJVM_DATA_TEXT.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_TEXT);
    }else if ( dataype == DataType.PLJVM_DATA_BYTEA.ordinal()) {
      type = new Type(DataType.PLJVM_DATA_BYTEA);
    }else if ( dataype == DataType.PLJVM_DATA_ARRAY.ordinal()) {
      type =  new Type(DataType.PLJVM_DATA_ARRAY);
    }else if ( dataype == DataType.PLJVM_DATA_UDT.ordinal()) {
      type =  new Type(DataType.PLJVM_DATA_UDT);
    }
    type.typeName=null;
    if (!byteBuf.isReadable(4)) return null;
    int strlen = byteBuf.readIntLE();
    if (strlen > 0) {
      if (!byteBuf.isReadable(strlen)) return null;
      type.typeName = byteBuf.readBytes(strlen).toString(Charset.defaultCharset());
    }
    if (dataype == DataType.PLJVM_DATA_ARRAY.ordinal() || dataype == DataType.PLJVM_DATA_UDT.ordinal() ){
      if (!byteBuf.isReadable(2)) return null;
      type.numSubtypes = byteBuf.readShortLE();
      type.subTypes = new Type[type.numSubtypes];
      for (int i= 0; i < type.numSubtypes; i++){
        type.subTypes[i] = getInstance(byteBuf);
        if ( type.subTypes[i] == null ) return null;
      }
    }
    return type;
  }
  public static Type getInstance(Class clazz) throws ClassNotFoundException {
    Type type = null;
    if (clazz == boolean.class || clazz == Boolean.class) {
      type = new Type(DataType.PLJVM_DATA_INT1);
      type.clazz = clazz;
    } else if (clazz == short.class || clazz == Short.class) {
      type = new Type(DataType.PLJVM_DATA_INT2);
      type.clazz = clazz;
    } else if (clazz == int.class || clazz == Integer.class) {
      type = new Type(DataType.PLJVM_DATA_INT4);
      type.clazz = clazz;
    } else if (clazz == long.class || clazz == Long.class) {
      type = new Type(DataType.PLJVM_DATA_INT8);
      type.clazz = clazz;
    } else if (clazz == float.class || clazz == Float.class) {
      type = new Type(DataType.PLJVM_DATA_FLOAT4);
      type.clazz = clazz;
    } else if (clazz == double.class || clazz == Double.class) {
      type = new Type(DataType.PLJVM_DATA_FLOAT8);
      type.clazz = clazz;
    } else if (clazz == String.class || clazz == String.class) {
      type = new Type(DataType.PLJVM_DATA_TEXT);
      type.clazz = clazz;
    } else if (clazz == byte[].class) {
      type = new Type(DataType.PLJVM_DATA_BYTEA);
      type.clazz = clazz;
    } else if (clazz.isArray()){
      type = new Type(DataType.PLJVM_DATA_ARRAY);
      type.clazz = clazz;
      type.numSubtypes = 1;
      type.subTypes=new Type[] {Type.getInstance(getArryTypeFromClass(clazz))};
    }

    return type;
  }

  private static Class getArrayType(Object array) {
    // drill down until we are at a non array type
    while(array.getClass().isArray() ){
      array= Array.get(array, 0);
    }
    return array.getClass();
  }

  static Class getArryTypeFromClass(Class clazz) throws ClassNotFoundException
  {
    String className = clazz.getName();
    int lastBracket = className.lastIndexOf('[');
    String arrayType = className.substring(lastBracket+1);
    if (arrayType.equals("Z")) return boolean.class;
    if (arrayType.equals("S")) return short.class;
    if (arrayType.equals("I")) return int.class;
    if (arrayType.equals("J")) return long.class;
    if (arrayType.equals("F")) return float.class;
    if (arrayType.equals("D")) return double.class;
    return Class.forName(arrayType);
  }
  public int getNumSubTypes(){
    return numSubtypes;
  }
  public Type[] getSubTypes(){
    return subTypes;
  }
  public Type getSubType(int n){
    return subTypes[n];
  }

  public DataType getDataType() {
    return dataType;
  }
  public Class getClazz() {
    return clazz;
  }
}
