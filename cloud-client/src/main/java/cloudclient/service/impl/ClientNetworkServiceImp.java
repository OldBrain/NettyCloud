package cloudclient.service.impl;

import cloudclient.service.CallBackService;
import cloudclient.network.pipelineclip.InitializedPipeline;
import cloudclient.network.pipelineclip.CommandPipeline;
import cloudclient.network.pipelineclip.PipelineForOutFiles;
import cloudclient.util.ClientPropertiesUtils;
import domain.commands.Command;
import domain.commands.ComName;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedFile;
import java.io.File;
import java.io.IOException;

public class ClientNetworkServiceImp {
  private ClientPropertiesUtils prop = new ClientPropertiesUtils();
  public static boolean isConnect = false;
  public SocketChannel channel;
   CallBackService onCommandReceivedCallback;
  InitializedPipeline commandPipeline = new CommandPipeline();
  InitializedPipeline outFilesPipeline = new PipelineForOutFiles();

  public ClientNetworkServiceImp(CallBackService onCommandReceivedCallback) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;

new Thread(new Runnable() {
  public void run() {
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel socketChannel) throws Exception {
              channel = socketChannel;
              ChannelPipeline p = channel.pipeline();
              p = commandPipeline.reloadClip(channel,null,onCommandReceivedCallback);

            }
          });
      int port = Integer.parseInt(prop.value("PORT"));
      String host = prop.value("HOST");

      ChannelFuture future = b.connect(host,port ).sync();
      future.channel().closeFuture().sync();
    } catch (Exception e) {
//    e.printStackTrace();
      System.out.println("Ошибка подключения к серверу");
    }finally {
      workerGroup.shutdownGracefully();
    }
  }
}).start();
  }

  public void sendCommandToServer(Command command) {
    ChannelPipeline p = channel.pipeline();

    if (command.commandName == ComName.TAKE_FILE_FROM_SERVER
        && command.commandArguments != null) {
      String fullPath = command.commandArguments[0];
      /**Размер передаваемого файла*/
      String size = command.commandArguments[1];


      /**Выделяем имя файла из строки commandArguments*/
      String[] f = command.commandArguments[0].split("\\\\");
      command.commandArguments[0] = f[f.length-1];
      command.commandArguments[1] = size;


/** Устанавливаем набор обработчиков для приема команд*/
      p = commandPipeline.reloadClip(channel,command,onCommandReceivedCallback);
/**Отправляем соманду на сервер*/
      channel.writeAndFlush(command);
/** Устанавливаем набор обработчиков для передачи файлов*/
      p = outFilesPipeline.reloadClip(channel,command,onCommandReceivedCallback);

/** Отправляем файл на сервер*/
      ChannelFuture future = null;
      File file = new File(fullPath);

      try {
        future = channel.writeAndFlush(new ChunkedFile(file));
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
      future.addListener((ChannelFutureListener) channelFuture -> {
        System.out.println("Finish write file: "+command.commandArguments[0]);
      });
/** Устанавливаем набор обработчиков для приема команд*/
      p = commandPipeline.reloadClip(channel,command,onCommandReceivedCallback);

    }

    if (command.commandName == ComName.GIVE_MI_FILE
        && command.commandArguments != null) {
      p = commandPipeline.reloadClip(channel,command,onCommandReceivedCallback);
      channel.writeAndFlush(command);
    }
    if (command.commandName == ComName.GIVE_TREE) {
      p = commandPipeline.reloadClip(channel,command,onCommandReceivedCallback);
      channel.writeAndFlush(command);
    }
    if (command.commandName==ComName.DELETE_FILE) {
      channel.writeAndFlush(command);
    }
    if (command.commandName == ComName.LOGIN) {
      channel.writeAndFlush(command);
    }
  }


}
