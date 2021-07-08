package cloudclient.service.impl.command;

import cloudclient.service.CallBackService;
import cloudclient.service.CommandService;
import domain.commands.CommandName;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
/**Команда получения списка файлов
 * с сервера и отображения их в MainController*/

public class GetFileTreeServerCommand implements CommandService {

  private CallBackService onCommandReceivedCallback;

  public GetFileTreeServerCommand(CallBackService onCommandReceivedCallback) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
  }

  @Override
  public Command processCommand(Command command, SocketChannel channel, ChannelPipeline pipeline) {
    onCommandReceivedCallback.callBack(command);
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.TAKE_TREE;
  }
}
