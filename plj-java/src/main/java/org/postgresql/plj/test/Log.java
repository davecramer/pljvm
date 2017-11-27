package org.postgresql.plj.test;

import java.util.logging.Logger;

public class Log {
  private static final Logger logger =  Logger.getLogger(Log.class.getName());

  public String log(String message){
    logger.info(message);
    return null;
  }
}
