package org.postgresql.plj.message;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionResponse extends Response{
  String message;
  String stacktrace;

  public ExceptionResponse(Throwable ex)
  {
    StringWriter writer = new StringWriter();
    ex.printStackTrace(new PrintWriter(writer));
    responseType = Response.EXCEPTION_RESPONSE;
    this.message = ex.getMessage();
    this.stacktrace = writer.toString();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStacktrace() {
    return stacktrace;
  }

  public void setStacktrace(String stacktrace) {
    this.stacktrace = stacktrace;
  }
}
