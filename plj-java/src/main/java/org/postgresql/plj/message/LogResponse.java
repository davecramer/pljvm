package org.postgresql.plj.message;

public class LogResponse extends Response {

  int level;
  String message;

  public LogResponse(int level, String message){
    responseType = Response.LOG_RESPONSE;
    this.level = level;
    this.message = message;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
