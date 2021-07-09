package server.handler;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.factory.Factory;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.CommandPipeline;
import server.network.pipelineclip.PipelineForInFiles;
import server.service.ServerCommandDictionaryService;
import java.util.Arrays;


public class ServerObjectHandler extends ChannelInboundHandlerAdapter {

  private SocketChannel channel;
  private InitPipeline filesPipeline = new PipelineForInFiles();
  private InitPipeline commandPipeline = new CommandPipeline();
  private ServerCommandDictionaryService dictionaryService;
  private Factory factory;
  ChannelPipeline pipeline;

  public ServerObjectHandler(SocketChannel channel) {
    this.channel = channel;
    factory = new Factory(channel);
    this.dictionaryService = factory.getCommandDictionaryService();

  }


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);

    if (ctx.writeAndFlush(new Command(CommandName.CONNECT_OK, null, null)).isDone()) {
      System.out.println("Клиент подключен!");
    }

  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);
//    SrvPropertiesUtils prop = new SrvPropertiesUtils();
    pipeline = ctx.pipeline();
    Command command = (Command) msg;
    dictionaryService.processCommand(command,pipeline);
    printCommand(command);
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
