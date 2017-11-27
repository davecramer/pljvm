package org.postgresql.plj;


import org.postgresql.plj.arg.Argument;
import org.postgresql.plj.message.CallRequest;
import org.postgresql.plj.util.Util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


import java.util.List;



public class PLJVMServerDecoder extends ByteToMessageDecoder {



    private RequestState state = RequestState.WAITING;
    CallRequest callRequest;
    int argNumber;


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        StringBuffer stringBuffer = null;
        int readerIndex = byteBuf.readerIndex();
        switch (state) {
            case WAITING:
                if (byteBuf.readByte() == 'C') {
                    state = RequestState.FUNCTION_NAME;
                    callRequest = new CallRequest();
                }
                break;
            case FUNCTION_NAME:
                stringBuffer = new StringBuffer();
                if ( Util.readString(stringBuffer, byteBuf) ) {
                    callRequest.setName(stringBuffer.length() == 0 ? null : stringBuffer.toString());
                    state = RequestState.FUNCTION_DEFINTION;
                }
                break;
            case FUNCTION_DEFINTION:
                stringBuffer = new StringBuffer();
                if ( Util.readString(stringBuffer, byteBuf) ) {
                    callRequest.setDefinition(stringBuffer.length() == 0 ? null : stringBuffer.toString());
                    state = RequestState.OID;
                }
                break;
            case OID:
                callRequest.setOid(byteBuf.readIntLE());
                state = RequestState.HAS_CHANGED;
                break;
            case HAS_CHANGED:
                callRequest.setHasChanged((byteBuf.readIntLE()==1));
                state = RequestState.RETURN_TYPE;
                break;
            case RETURN_TYPE:
                callRequest.setReturnType(Type.getInstance(byteBuf));
                state = RequestState.SET_RETURNING;
                break;
            case SET_RETURNING:
                callRequest.setSetReturning((byteBuf.readIntLE()==1));
                state = RequestState.NUM_ARGUMENTS;
                break;
            case NUM_ARGUMENTS:
                callRequest.setNumArgs(byteBuf.readIntLE());
                if (callRequest.getNumArgs() == 0 ){
                    list.add(callRequest);
                    state = RequestState.WAITING;
                }else {
                    state = RequestState.ARGUMENTS;
                    argNumber = 0;
                }
                break;
            case ARGUMENTS:
                //TODO deal with no arguments
                Argument argument;
                if (argNumber < callRequest.getNumArgs()) {
                    argument = Argument.readArgument(byteBuf);

                    if (argument == null) {
                      // reset it and wait for more data.
                      byteBuf.readerIndex(readerIndex);
                    }else {
                      callRequest.setArg(argNumber++, argument);
                      if (argNumber >= callRequest.getNumArgs()) {
                        list.add(callRequest);
                      }
                    }
                }


        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private enum RequestState {
        WAITING,
        FUNCTION_NAME,
        FUNCTION_DEFINTION,
        OID,
        HAS_CHANGED,
        RETURN_TYPE,
        RETURN_NAME,
        SET_RETURNING,
        NUM_ARGUMENTS,
        ARGUMENTS
    }
}
