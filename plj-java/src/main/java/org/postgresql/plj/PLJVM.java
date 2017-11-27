package org.postgresql.plj;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.util.logging.*;

/**
 * PLJVM
 *
 */
public class PLJVM
{

  private static Logger logger=Logger.getLogger(PLJVM.class.getName());

  private int port;

  public PLJVM(int port)
  {
    this.port = port;
  }


  public void run() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class) // (3)
              .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                  LogManager.getLogManager().reset();
                  Logger rootLogger = LogManager.getLogManager().getLogger("");
                  rootLogger.addHandler(new PLJVMLogHandler(ch.pipeline()));
                  ch.pipeline().addLast(new PLJVMServerDecoder());
                  ch.pipeline().addLast(new PLJVMServerEncoder());
                  ch.pipeline().addLast(new JVMCallHandler());
                }
              })
              .option(ChannelOption.SO_BACKLOG, 128)          // (5)
              .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port).sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static void main( String[] args ) throws Exception {

    new PLJVM(8000).run();

  }
}
