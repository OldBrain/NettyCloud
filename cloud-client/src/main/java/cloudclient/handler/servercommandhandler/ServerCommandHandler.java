package cloudclient.handler.servercommandhandler;

import cloudclient.factory.Factory;
import cloudclient.front.controlers.MainController;
import cloudclient.network.pipelineclip.CommandPipeline;
import cloudclient.network.pipelineclip.InitializedPipeline;
import cloudclient.network.pipelineclip.PipelineForInFiles;
import cloudclient.service.CallBackService;
import cloudclient.service.CommandDictionaryService;
import cloudclient.service.impl.command.GetFileTreeServerCommand;
import cloudclient.service.impl.command.ServerAcceptedFileCommand;
import domain.commands.CommandName;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.io.File;
import java.util.Arrays;

public class ServerCommandHandler {
  private CallBackService onCommandReceivedCallback;
  private InitializedPipeline inFilesPipeline = new PipelineForInFiles();
  private InitializedPipeline commandPipeline = new CommandPipeline();
  private CommandDictionaryService dictionaryService;
  private Factory factory;

  public ServerCommandHandler(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    factory = new Factory(onCommandReceivedCallback, channel);
    this.dictionaryService = factory.getCommandDictionaryService();
  }

  public void commandHandler(ChannelPipeline pipeline, Command command) {
    dictionaryService.processCommand(command, pipeline);
    printCommand(command);
  }

  public void printCommand(Command command) {
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
