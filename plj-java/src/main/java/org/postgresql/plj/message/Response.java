package org.postgresql.plj.message;

public class Response {
  public static final byte CALL_RESPONSE='R';
  public static final byte EXCEPTION_RESPONSE='E';
  public static final byte LOG_RESPONSE='L';

  byte          responseType;

  public byte getResponseType() {
    return responseType;
  }

  public void setResponseType(byte responseType) {
    this.responseType = responseType;
  }
}
