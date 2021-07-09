package cloudclient.service.impl.command;

import cloudclient.service.CallBackService;
import cloudclient.service.CommandService;
import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Команда подтверждения успешной записи файла на сервер
 */
public class ServerAcceptedFileCommand implements CommandService {

  private CallBackService onCommandReceivedCallback;

  public ServerAcceptedFileCommand(CallBackService onCommandReceivedCallback) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
  }

  @Override
  public Command processCommand(Command command, SocketChannel channel, ChannelPipeline pipeline) {
    /**Останавливаем ProgressBar*/
    onCommandReceivedCallback.callBack(new Command(
        CommandName.SERVER_FILE_ACCEPTED));
    /**Отправляем запрос на получение спсиска файлов с сервера*/
    channel.writeAndFlush(new Command(CommandName.GIVE_TREE));
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.SERVER_FILE_ACCEPTED;
  }
}
