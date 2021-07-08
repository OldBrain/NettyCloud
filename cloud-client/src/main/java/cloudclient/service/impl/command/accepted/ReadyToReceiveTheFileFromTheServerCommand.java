package cloudclient.service.impl.command.accepted;

import cloudclient.front.controlers.MainController;
import cloudclient.network.pipelineclip.InitializedPipeline;
import cloudclient.network.pipelineclip.PipelineForInFiles;
import cloudclient.service.CallBackService;
import cloudclient.service.CommandService;
import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.io.File;

/**
 * Команад подготовки клиента к получению файла с сервера
 */
public class ReadyToReceiveTheFileFromTheServerCommand implements CommandService {

  private InitializedPipeline inFilesPipeline = new PipelineForInFiles();
  private CallBackService onCommandReceivedCallback;
  private SocketChannel channel;

  public ReadyToReceiveTheFileFromTheServerCommand(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
  }

  @Override
  public Command processCommand(Command command, SocketChannel channel, ChannelPipeline pipeline) {
    deleteFileIfItExists(command);
    pipeline = inFilesPipeline.reloadClip(channel, command, onCommandReceivedCallback);
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.TAKE_FILE_FROM_SERVER;
  }

  private void deleteFileIfItExists(Command command) {
    File file = new File(MainController.dirPath + command.commandArguments[0]);
    /**Надо вставить запрос на перезапись*/
    if (file.delete()) {
      System.out.println("Файл удален!!!");
    }
  }
}
