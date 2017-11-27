package org.postgresql.plj.message;

import org.postgresql.plj.Type;

public class CallResponse extends Response{
  int           numRows;
  int           numCols;
  Type types[];
  String        names[];
  Object[][]      rows;


  public CallResponse(){
    responseType = Response.CALL_RESPONSE;
  }

  public void setNumRowsCols(int numRows, int numCols)
  {
    this.numRows = numRows;
    this.numCols = numCols;
    this.rows = new Object[numRows][numCols];
  }
  public int getNumRows() {
    return numRows;
  }

  public int getNumCols() {
    return numCols;
  }

  public void setTypes(Type [] types){
    this.types = types;
  }
  public Type[] getTypes() {
    return types;
  }
  public void setNames(String []names){
    this.names = names;
  }
  public String[] getNames() {
    return names;
  }

  public Object[][] getRows() {
    return rows;
  }
  public Object getObject(int row, int col){
    return rows[row][col];
  }
  public void setRow(int row, Object []o){
    rows[row] = o;
  }
}
