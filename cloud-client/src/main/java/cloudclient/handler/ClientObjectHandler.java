package cloudclient.handler;

import cloudclient.handler.servercommandhandler.ServerCommandHandler;
import cloudclient.service.CallBackService;
import cloudclient.front.controlers.MainController;
import cloudclient.service.impl.ClientNetworkServiceImp;
import cloudclient.network.pipelineclip.CommandPipeline;
import cloudclient.network.pipelineclip.InitializedPipeline;
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
  private InitializedPipeline inFilesPipeline = new PipelineForInFiles();
  private InitializedPipeline commandPipeline = new CommandPipeline();
  private ServerCommandHandler serverCommandHandler;

  public ClientObjectHandler(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
    serverCommandHandler = new ServerCommandHandler(onCommandReceivedCallback, channel);
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    super.channelRegistered(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);
    ChannelPipeline pipeline = ctx.pipeline();
    Command command = (Command) msg;

    if (command.commandName == ComName.CONNECT_OK) {
      ClientNetworkServiceImp.isConnect = true;
      serverCommandHandler.printCommand(command);
      if (onCommandReceivedCallback != null) {
        onCommandReceivedCallback.callBack(command);
      }
    }

    serverCommandHandler.commandHandler(pipeline, command);
  }

}

