package org.postgresql.plj.test;

public class Bool {
    public boolean valueOf(boolean b)
    {
        return b;
    }
    public boolean[]xor(boolean[] arr, boolean b){
        for (int i=0; i<arr.length;i++){
            arr[i]^=b;
        }
        return arr;
    }

}
