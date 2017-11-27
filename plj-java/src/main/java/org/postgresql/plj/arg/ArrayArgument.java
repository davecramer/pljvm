package org.postgresql.plj.arg;

import org.postgresql.plj.Type;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Array;

public class ArrayArgument extends Argument {

    public ArrayArgument(StringBuffer name, Object value){
        super(name);
        clazz = value.getClass();
        this.value = value;
    }
    public static  ArrayArgument getInstance(StringBuffer name, Type type, ByteBuf byteBuf)
    {

        if (byteBuf.readableBytes()>=1) {
            if ( byteBuf.readByte() == 'D' ) {

                if (!byteBuf.isReadable(4)) return null;
                int ndims = byteBuf.readIntLE();

                if (!byteBuf.isReadable(4*ndims)) return null;
                int dims[] = new int[ndims];

                for (int i=0; i<ndims; i++){
                    dims[i] = byteBuf.readIntLE();
                }

                Object array=null;
                if ( dims.length == 1) {
                    array = Array.newInstance(type.getClazz(), dims[0]);
                    for ( int i=0; i<dims[0]; i++){
                        Array.set(array, i, Argument.readArgument(type, name, byteBuf).value);
                    }
                }else if ( dims.length == 2 ) {
                    array = Array.newInstance(type.getClazz(), dims[0], dims[1]);

                    for ( int i=0; i< dims[0]; i++) {
                        Object row = Array.get(array, i);
                        for (int j=0; j< dims[1]; j++) {
                            Array.set(row, j, Argument.readArgument(type, name, byteBuf).value);
                        }
                    }
                }else if ( dims.length == 3 ) {
                    array = Array.newInstance(int.class, dims[0], dims[1], dims[2]);
                    for ( int i=0; i< dims[0]; i++) {
                        Object row = Array.get(array, i);
                        for (int j=0; j< dims[1]; j++) {
                            Object z = Array.get(row, j);
                            for (int k=0; k < dims[2]; k++ ){
                                Array.set(z, k, Argument.readArgument(type, name, byteBuf).value);
                            }
                        }
                    }

                }

                return  new ArrayArgument(name, array);
            } else {
                return new ArrayArgument(name, null);
            }
        }
        return null;
    }

}
