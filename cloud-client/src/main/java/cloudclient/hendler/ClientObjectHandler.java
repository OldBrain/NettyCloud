package cloudclient.hendler;

import cloudclient.service.CallBackService;
import cloudclient.front.MainController;
import cloudclient.service.impl.ClientNetworkServiceImp;
import cloudclient.network.pipelineclip.CommandPipeline;
import cloudclient.network.pipelineclip.InitPipeline;
import cloudclient.network.pipelineclip.PipelineForInFiles;
import domain.commands.Command;
import domain.commands.ComName;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.io.File;
import java.util.Arrays;


public class ClientObjectHandler extends ChannelInboundHandlerAdapter {

  CallBackService onCommandReceivedCallback;
  private SocketChannel channel;
  private InitPipeline inFilesPipeline = new PipelineForInFiles();
  private InitPipeline commandPipeline = new CommandPipeline();

  public ClientObjectHandler(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    super.channelRegistered(ctx);

  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);
    ChannelPipeline p = ctx.pipeline();
    Command command = (Command) msg;

    if (command.commandName == ComName.CONNECT_OK) {
      ClientNetworkServiceImp.isConnect = true;
      if (onCommandReceivedCallback != null) {
        onCommandReceivedCallback.callBack(command);
      }

      printCommand(command);
    }

    if (command.commandName == ComName.TAKE_TREE) {
      if (onCommandReceivedCallback != null) {
        onCommandReceivedCallback.callBack(command);
        printCommand(command);
      }

    }

    if (command.commandName == ComName.TAKE_FILE_FROM_SERVER) {

      File file = new File(MainController.dirPath + command.commandArguments[0]);
      /**Надо вставить запрос на перезапись*/
      if (file.delete()) {
        System.out.println("Файл удален!!!");
      }
      printCommand(command);
      p = inFilesPipeline.reloadClip(channel, command, onCommandReceivedCallback);
    }

    if (command.commandName == ComName.SERVER_FILE_ACCEPTED) {
/**Останавливаем ProgressBar*/
      onCommandReceivedCallback.callBack(new Command(
          ComName.SERVER_FILE_ACCEPTED,
          null, null));
      printCommand(command);

      /**Отправляем запрос на получение спсиска файлов с сервера*/
      channel.writeAndFlush(new Command(ComName.GIVE_TREE));
    }

    if (command.commandName == ComName.SERVER_FILE_DELETE_OK) {
      printCommand(command);
    }

    if (command.commandName == ComName.SERVER_FILE_DELETE_NO) {
      printCommand(command);

    }

    if (command.commandName == ComName.LOGIN_OK) {
     printCommand(command);
        onCommandReceivedCallback.callBack(command);
    }


  }

  private void printCommand(Command command) {
    if (command.commandArguments == null) {
      System.out.println("Получена команда с сервера: "
          + command.commandName);
    }
    if (command.commandArguments != null) {
      System.out.print("Получена команда с сервера: "
          + command.commandName);

      System.out.println(Arrays.toString(command.commandArguments));
    }
  }
}

