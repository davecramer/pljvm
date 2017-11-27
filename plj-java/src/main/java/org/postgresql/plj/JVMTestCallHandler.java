package org.postgresql.plj;

import org.postgresql.plj.message.CallRequest;
import org.postgresql.plj.message.CallResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class JVMTestCallHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    CallRequest callRequest = (CallRequest)msg;
    CallResponse callResponse = new CallResponse();
    callResponse.setNames(new String[] {"col1", "col2", "col3", "col4", "col5", "col6", "col7"});
    callResponse.setTypes(new Type[] {new Type(DataType.PLJVM_DATA_INT1), new Type(DataType.PLJVM_DATA_INT2), new Type(DataType.PLJVM_DATA_INT4),
                                     new Type(DataType.PLJVM_DATA_INT8), new Type(DataType.PLJVM_DATA_FLOAT4), new Type(DataType.PLJVM_DATA_FLOAT8),
                                     new Type(DataType.PLJVM_DATA_TEXT)});

    callResponse.setNumRowsCols(1,callResponse.getTypes().length);

    callResponse.setRow(0, new Object[] { new Byte((byte)1), new Short((short)2), new Integer(3), new Long(4),
                                          new Float(123.4), new Double(567.8), new String("Hello World")});

    ctx.writeAndFlush(callResponse);
  }
}
