package cloudclient.handler.servercommandhandler;

import cloudclient.front.controlers.MainController;
import cloudclient.network.pipelineclip.CommandPipeline;
import cloudclient.network.pipelineclip.InitializedPipeline;
import cloudclient.network.pipelineclip.PipelineForInFiles;
import cloudclient.service.CallBackService;
import domain.commands.ComName;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.io.File;
import java.util.Arrays;

public class ServerCommandHandler {
  private CallBackService onCommandReceivedCallback;
  private SocketChannel channel;
  private InitializedPipeline inFilesPipeline = new PipelineForInFiles();
  private InitializedPipeline commandPipeline = new CommandPipeline();

  public ServerCommandHandler(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
  }

  public void commandHandler(ChannelPipeline pipeline, Command command) {
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
      pipeline = inFilesPipeline.reloadClip(channel, command, onCommandReceivedCallback);
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
