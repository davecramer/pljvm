package org.postgresql.plj.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Timestamp {
  public String valueOf(String  t) { return t;}
  public String now() {
    Calendar calendar = Calendar.getInstance();
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(calendar.getTime());
  }
}
