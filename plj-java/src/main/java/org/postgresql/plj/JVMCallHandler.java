package org.postgresql.plj;

import static org.postgresql.plj.util.PostgresLogLevel.DEBUG1;
import static org.postgresql.plj.util.PostgresLogLevel.DEBUG2;
import static org.postgresql.plj.util.PostgresLogLevel.DEBUG3;
import static org.postgresql.plj.util.PostgresLogLevel.DEBUG4;
import static org.postgresql.plj.util.PostgresLogLevel.DEBUG5;
import static org.postgresql.plj.util.PostgresLogLevel.ERROR;
import static org.postgresql.plj.util.PostgresLogLevel.INFO;
import static org.postgresql.plj.util.PostgresLogLevel.NOTICE;
import static org.postgresql.plj.util.PostgresLogLevel.WARNING;

import org.postgresql.plj.arg.Argument;
import org.postgresql.plj.message.CallRequest;
import org.postgresql.plj.message.CallResponse;
import org.postgresql.plj.message.ExceptionResponse;
import org.postgresql.plj.message.LogResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JVMCallHandler extends ChannelInboundHandlerAdapter {

    private final ClassLoader classLoader;

    public JVMCallHandler(){
        super();
        this.classLoader = ClassLoader.getSystemClassLoader();

    }

    private Class<?> findClass(String className) throws ClassNotFoundException
    {
        return Class.forName(className);
    }

    private Method findMethod(Class clazz, String methodName, Class<?> []args) throws NoSuchMethodException
    {
        Method method;
        method = clazz.getMethod(methodName, args);
        return method;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        CallRequest callRequest = (CallRequest)msg;
        String functionName = callRequest.getDefinition().trim();
        Class<?> argTypes[] = new Class[callRequest.getNumArgs()];
        Object args[] = new Object[callRequest.getNumArgs()];

        int i=0;

        for (Argument arg:callRequest.getArgs()){
            argTypes[i] = arg.getClazz();
            args[i++] = arg.getValue();
        }

        int pos = functionName.lastIndexOf('.');
        String className = functionName.substring(0, pos);
        String methodName = functionName.substring(pos+1);
        Object ret = null;
        try {

            Class <?> c = findClass(className);
            Object o = c.newInstance();
            Method method = c.getMethod(methodName, argTypes );
            ret = method.invoke(o, args);
            sendCallResponse(ctx, callRequest.getName(), ret);
        } catch (Throwable ex){
            sendExceptionResponse(ctx, ex);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LogRecord logRecord = (LogRecord)evt;
        StringBuilder sb = new StringBuilder();
        sb.append(logRecord.getSourceClassName())
            .append('-')
            .append(logRecord.getSourceMethodName())
            .append('-')
            .append(logRecord.getMessage());

        if (logRecord.getLevel().intValue() == Level.ALL.intValue() ){
            sendLogResponse(ctx, DEBUG5, sb.toString());
        } else if (logRecord.getLevel().intValue() == Level.FINEST.intValue() ){
            sendLogResponse(ctx, DEBUG4, sb.toString());
        } else if (logRecord.getLevel().intValue() == Level.FINER.intValue() ){
            sendLogResponse(ctx, DEBUG3, sb.toString());
        }else if (logRecord.getLevel().intValue() == Level.FINE.intValue() ){
            sendLogResponse(ctx, DEBUG2, sb.toString());
        }else if (logRecord.getLevel().intValue() == Level.INFO.intValue() ){
            sendLogResponse(ctx, INFO, sb.toString());
        }else if (logRecord.getLevel().intValue() == Level.WARNING.intValue() ){
            sendLogResponse(ctx, WARNING, sb.toString());
        }else if (logRecord.getLevel().intValue() == Level.SEVERE.intValue() ) {
            sendLogResponse(ctx, ERROR, sb.toString());
        }else {
            sendLogResponse(ctx, INFO, "Unknown error");
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        sendExceptionResponse(ctx, cause);
    }

    private void sendLogResponse(ChannelHandlerContext ctx, int level, String message){
        LogResponse logResponse = new LogResponse(level, message);
        ctx.writeAndFlush(logResponse);
    }
    private void sendCallResponse(ChannelHandlerContext ctx, String name,  Object ret) throws ClassNotFoundException{
        CallResponse callResponse = new CallResponse();
        callResponse.setNumRowsCols(1,1);
        callResponse.setRow(0, new Object[] {ret});
        if ( ret == null ) {
            callResponse.setTypes(new Type[] { Type.getInstance(String.class)});
        }else {
            callResponse.setTypes(new Type[]{Type.getInstance(ret.getClass())});
        }
        callResponse.setNames(new String[] {name});
        ctx.writeAndFlush(callResponse);
    }
    private void sendExceptionResponse(ChannelHandlerContext ctx, Throwable ex)
    {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex);
        ctx.writeAndFlush(exceptionResponse);

    }
}
