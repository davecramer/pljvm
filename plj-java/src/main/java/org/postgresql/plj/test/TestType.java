package org.postgresql.plj.test;

public class TestType {
  boolean a;
  short b;
  int c;
  long d;
  float e;
  double f;
  double g;
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

  public void setB(Short b) {
    this.b = b;
  }

  public int getC() {
    return c;
  }

  public void setC(Integer c) {
    this.c = c;
  }

  public long getD() {
    return d;
  }

  public void setD(Long d) {
    this.d = d;
  }

  public float getE() {
    return e;
  }

  public void setE(Float e) {
    this.e = e;
  }

  public double getF() {
    return f;
  }

  public void setF(Double f) {
    this.f = f;
  }

  public double getG() {
    return g;
  }

  public void setG(Double g) {
    this.g = g;
  }

  public String getH() {
    return h;
  }

  public void setH(String h) {
    this.h = h;
  }
}
