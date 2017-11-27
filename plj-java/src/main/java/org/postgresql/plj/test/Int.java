package org.postgresql.plj.test;

public class Int {
    public Integer add(Integer a, Integer b) {return a + b;}
    public Short add(Short a, Short b) { return (short)(a+b); }
    public Long add(Long a, Long b) { return a+b; }
    public Float add(Float a, Float b) { return a+b; }
    public Double add(Double a, Float b) { return a+b; }

    public Integer addarr(short[][]array) {
        int sum=0;
        for (int i = 0; i < array.length; i++){
            short row[] = array[i];
            for (int j=0; j<row.length; j++){
                sum+=row[j];
            }
        }

        return sum;
    }

    public Integer addarr(int[][]array) {
        int sum=0;
        for (int i = 0; i < array.length; i++){
            int row[] = array[i];
            for (int j=0; j<row.length; j++){
                sum+=row[j];
            }
        }

        return sum;
    }

    public Integer addarr(long[][]array) {
        int sum=0;
        for (int i = 0; i < array.length; i++){
            long row[] = array[i];
            for (int j=0; j<row.length; j++){
                sum+=row[j];
            }
        }

        return sum;
    }
    public int[] add(int []arr, Integer v) {
        for ( int i=0; i < arr.length; i++) {
            arr[i] += v;
        }
        return arr;
    }
    public short[] add(short []arr, Short v) {
        for ( int i=0; i < arr.length; i++) {
            arr[i] = (short)(arr[i]+v);
        }
        return arr;
    }
    public long[] add(long []arr, Integer v) {
        for ( int i=0; i < arr.length; i++) {
            arr[i] += v;
        }
        return arr;
    }
}
