package org.postgresql.plj.test;

public class TestType {
  boolean a;
  short b;
  int c;
  long d;
  float e;
  double f;
  float n;
  String h;

  public TestType echo(TestType testType) {return testType;}

  public boolean isA() {
    return a;
  }

  public void setA(boolean a) {
    this.a = a;
  }

  public short getB() {
    return b;
  }

  public void setB(short b) {
    this.b = b;
  }

  public int getC() {
    return c;
  }

  public void setC(int c) {
    this.c = c;
  }

  public long getD() {
    return d;
  }

  public void setD(long d) {
    this.d = d;
  }

  public float getE() {
    return e;
  }

  public void setE(float e) {
    this.e = e;
  }

  public double getF() {
    return f;
  }

  public void setF(double f) {
    this.f = f;
  }

  public float getN() {
    return n;
  }

  public void setN(float n) {
    this.n = n;
  }

  public String getH() {
    return h;
  }

  public void setH(String h) {
    this.h = h;
  }
}
