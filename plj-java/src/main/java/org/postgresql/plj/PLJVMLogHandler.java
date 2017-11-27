package org.postgresql.plj;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PLJVMLogHandler extends Handler {

  ChannelPipeline pipeline;
  PLJVMLogHandler(ChannelPipeline pipeline)
  {
    this.pipeline = pipeline;

  }
  @Override
  public void publish(LogRecord record) {
    pipeline.fireUserEventTriggered(record);
  }

  @Override
  public void flush() {

  }

  @Override
  public void close() throws SecurityException {

  }
}
