package org.postgresql.plj;

import org.postgresql.plj.message.CallResponse;
import org.postgresql.plj.message.ExceptionResponse;
import org.postgresql.plj.message.LogResponse;
import org.postgresql.plj.message.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PLJVMServerEncoder extends MessageToByteEncoder {
  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf)
      throws Exception {

    byteBuf = byteBuf.ensureWritable(1024);
    switch(((Response)o).getResponseType()) {
      case Response.CALL_RESPONSE:
        sendCallResponse(byteBuf, (CallResponse) o);
        break;
      case Response.EXCEPTION_RESPONSE:
        sendExceptionResponse(byteBuf, (ExceptionResponse) o);
        break;
      case Response.LOG_RESPONSE:
        sendLogResponse(byteBuf, (LogResponse)o);
        break;
    }
  }

  void sendLogResponse(ByteBuf byteBuf, LogResponse logResponse){
    byteBuf.writeByte(logResponse.getResponseType());
    byteBuf.writeIntLE(logResponse.getLevel());
    sendString(byteBuf, logResponse.getMessage());
  }

  void sendExceptionResponse(ByteBuf byteBuf, ExceptionResponse exceptionResponse){
    byteBuf.writeByte(exceptionResponse.getResponseType());
    sendString(byteBuf, exceptionResponse.getMessage());
    sendString(byteBuf, exceptionResponse.getStacktrace());
  }

  void sendCallResponse(ByteBuf byteBuf, CallResponse callResponse){

    byteBuf.writeByte(callResponse.getResponseType());
    byteBuf.writeIntLE(callResponse.getNumRows());
    byteBuf.writeIntLE(callResponse.getNumCols());

    for( int i=0; i < callResponse.getTypes().length; i++) {
      sendType(byteBuf, callResponse.getTypes()[i]);

      byteBuf.writeIntLE(callResponse.getNames()[i].length());
      byteBuf.writeBytes(callResponse.getNames()[i].getBytes());
    }


    for ( int row = 0; row < callResponse.getNumRows(); row ++ ) {
      for (int col = 0; col < callResponse.getNumCols(); col++ ) {
        Object obj = callResponse.getObject(row, col );
        sendObject(byteBuf, callResponse.getTypes()[col], obj);
      }
    }
    byteBuf.writeByte('N');
  }

  private void sendObject(ByteBuf byteBuf, Type type, Object object){
    if( object == null ) {
      byteBuf.writeByte('N');
      return;
    }else {
      byteBuf.writeByte('D');
    }

    switch (type.dataType){
      case PLJVM_DATA_INT1:
        byteBuf.writeBoolean((Boolean)object);
        break;
      case PLJVM_DATA_INT2:
        byteBuf.writeShortLE((Short)object);
        break;
      case PLJVM_DATA_INT4:
        byteBuf.writeIntLE((Integer)object);
        break;
      case PLJVM_DATA_INT8:
        byteBuf.writeLongLE((Long)object);
        break;
      case PLJVM_DATA_FLOAT4:
        byteBuf.writeFloatLE((Float)object);
        break;
      case PLJVM_DATA_FLOAT8:
        byteBuf.writeDoubleLE((Double)object);
        break;
      case PLJVM_DATA_TEXT:
        sendString(byteBuf, (String)object);
        break;
      case PLJVM_DATA_BYTEA:
        byte[]  bytes = (byte []) object;
        byteBuf.writeIntLE(bytes.length);
        byteBuf.writeBytes(bytes);
        break;
      case PLJVM_DATA_ARRAY:
        sendArray(byteBuf, type.getSubType(0), object);
        break;

    }

  }

  private  int[] getDimensions(Object array) {

    ArrayList<Integer> dims =  new ArrayList();

    while ( array.getClass().isArray() ) {
      dims.add(Array.getLength(array));
      array = Array.get(array, 0);
    }

    int []ret = new int[dims.size()];
    for (int i=0; i< dims.size(); i++){
      ret[i]=dims.get(i);

    }
    return ret;
  }

  /**
   *
   * @param byteBuf
   * @param arr
   */
  private void sendArray(ByteBuf byteBuf, Type type, Object arr) {
    int [] dims = getDimensions(arr);
    // send ndims
    byteBuf.writeIntLE(dims.length);
    // send dims

    for (int i = 0; i < dims.length; i++){
      byteBuf.writeIntLE(dims[i]);
    }
    sendArrayFlat(byteBuf, type, arr);
  }

  /**
   *
   * @param byteBuf
   * @param dataType
   * @param arr
   */
  private void sendArrayFlat(ByteBuf byteBuf, Type dataType, Object arr) {
      if (arr.getClass().isArray()) {
        for (int i = 0; i < Array.getLength(arr); i++) {
          sendArrayFlat(byteBuf, dataType, Array.get(arr, i));
        }
      } else {
        sendObject(byteBuf, dataType, arr);
      }
  }

  /**
   *
   * @param byteBuf
   * @param string
   */
  private void sendString(ByteBuf byteBuf, String string){
    if (string == null){
      byteBuf.writeIntLE(-1);
    }else {
      byteBuf.writeIntLE(string.length());
      byteBuf.writeBytes(string.getBytes());
    }
  }

  /**
   *
   * @param byteBuf
   * @param type
   */
  private void sendType(ByteBuf byteBuf, Type type){
    byteBuf.writeByte( type.dataType.ordinal() );
    sendString(byteBuf, type.typeName);
    if (type.getNumSubTypes() > 0) {
      byteBuf.writeShortLE(type.getNumSubTypes());

      for (Type type1 : type.getSubTypes()) {
        byteBuf.writeByte(type1.dataType.ordinal());
        sendString(byteBuf, type1.typeName);
      }
    }

  }

}
