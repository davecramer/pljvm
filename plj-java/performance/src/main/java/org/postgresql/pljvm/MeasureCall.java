package org.postgresql.pljvm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MeasureCall {
  public static void main(String []args) throws  Exception{
    Connection connection = createConnection("localhost", 5432, "test");

  }
  static Connection createConnection(String host, int port, String database) throws SQLException{
    return DriverManager.getConnection(host+':'+port, "test","");
  }

  static boolean createLanguage(Connection connection) throws Exception{
    try ( Statement statement = connection.createStatement() ){
      statement.execute("CREATE EXTENSION PLJVM");
    }
    return true;
  }

}
