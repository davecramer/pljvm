package org.postgresql.plj.util;

public class PostgresLogLevel {
  public static final int DEBUG5 = 10;			/* Debugging messages, in categories of  decreasing detail. */
  public static final int DEBUG4 = 11;
  public static final int DEBUG3 = 12;
  public static final int DEBUG2 = 13;
  public static final int DEBUG1 = 14;		/* used by GUC debug_* variables */
  public static final int LOG		 = 15;			/* Server operational messages; sent only to
								 * server log by default. */
  public static final int  LOG_SERVER_ONLY = 16;		/* Same as LOG for server reporting, but never
								 * sent to client. */
  public static final int  COMMERROR = 	LOG_SERVER_ONLY; /* Client communication problems; same as
									 * LOG for server reporting, but never
									 * sent to client. */
  public static final int  INFO	 = 17;			/* Messages specifically requested by user (eg
								 * VACUUM VERBOSE output); always sent to
								 * client regardless of client_min_messages,
								 * but by default not sent to server log. */
  public static final int  NOTICE = 18;			/* Helpful messages to users about query
								 * operation; sent to client and not to server
								 * log by default. */
  public static final int  WARNING = 19;			/* Warnings.  NOTICE is for expected messages
								 * like implicit sequence creation by SERIAL.
								 * WARNING is for unexpected messages. */
  public static final int  ERROR = 20;			/* user error - abort transaction; return to
								 * known state */
  public static final int  FATAL = 21;			/* fatal error - abort process */
  public static final int  PANIC = 22;			/* take down the other backends with me */
}
