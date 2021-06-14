package server.hendler;

import domain.commands.Command;
import domain.commands.ComName;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.executingcommands.SendCommandsToClient;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.CommandPipeline;
import server.network.pipelineclip.PipelineForInFiles;
import server.util.SrvProperties;

import java.io.File;
import java.util.Arrays;


public class ServerObjectHandler extends ChannelInboundHandlerAdapter {
  Command command;

  private SendCommandsToClient sendToClient = new SendCommandsToClient();
  private SocketChannel channel;
  private InitPipeline FilesPipeline = new PipelineForInFiles();
  private InitPipeline commandPipeline = new CommandPipeline();

  public ServerObjectHandler(SocketChannel channel) {
    this.channel = channel;
  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);

    if (ctx.writeAndFlush(new Command(ComName.CONNECT_OK, null, null)).isDone()) {
      System.out.println("Клиент подключен!");
    }

  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);
    SrvProperties prop = new SrvProperties();

    ChannelPipeline p = ctx.pipeline();
    Command command = (Command) msg;
    switch (command.commandName) {
      case GIVE_TREE:
        printCommand(command);
        sendToClient.getTree(channel);
        break;

      case TAKE_FILE_FROM_SERVER:
        printCommand(command);
        /**Если файл существует то будет перезаписан*/
        String fileP = prop.value("MAIN_DIR") + "\\" + command.commandArguments[0];
        File file = new File(fileP);
        if (file.delete()) {
          System.out.println("Файл будет перезаписан на сервере");
        }
        p = FilesPipeline.reloadClip(channel, command);
        break;

      case GIVE_MI_FILE:
        printCommand(command);
        sendToClient.sendFileToClient(command, channel);
        break;

      case CLIENT_FILE_ACCEPTED:
        printCommand(command);

        break;
      case DELETE_FILE:
        printCommand(command);
        sendToClient.deleteFile(command,channel);
        break;
      case LOGIN:
        sendToClient.loginOK(channel);
        printCommand(command);
        break;

        default:
        break;
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
        System.out.println( Arrays.toString(command.commandArguments));
      }


    }


}
