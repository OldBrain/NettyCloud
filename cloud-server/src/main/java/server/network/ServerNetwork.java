package server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import server.network.pipelineclip.CommandPipeline;
import server.util.SrvPropertiesUtils;

public class ServerNetwork {
  private SocketChannel channel;
  SrvPropertiesUtils prop = new SrvPropertiesUtils();
  public ServerNetwork() {

    EventLoopGroup authorizationGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    EventLoopGroup bigFileGroup = new NioEventLoopGroup();
    try {

      int inetPort= Integer.parseInt(prop.value("inetPort"));

      ServerBootstrap b = new ServerBootstrap();

      b.group(workerGroup,bigFileGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel socketChannel) throws Exception {
              channel = socketChannel;
              ChannelPipeline p = channel.pipeline();
              p = new CommandPipeline().reloadClip(channel,null);
            }


          });


      ChannelFuture future = b.bind(inetPort).sync();
      System.out.println("Сервер запущен.");
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      authorizationGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      System.out.println("Сервер остановлен.");
    }
  }

}
