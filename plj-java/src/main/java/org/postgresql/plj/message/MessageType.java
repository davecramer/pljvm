package org.postgresql.plj.message;

public enum MessageType {
  TRIGGER_REQUEST(Character.getNumericValue('T')),
  CALLREQ(Character.getNumericValue('C')),
  RESULT(Character.getNumericValue('R')),
  EXCEPTION(Character.getNumericValue('E')),
  SQL(Character.getNumericValue('S')),
  LOG(Character.getNumericValue('L')),
  TUPLRES(Character.getNumericValue('U')),
  TRANSEVENT(Character.getNumericValue('V')),
  PING(Character.getNumericValue('P')),
  EOF(0);

  private final short value;

  private MessageType(int i) {
    value = (short)i;
  }

  public short getValue()
  {
    return value;
  }
}
