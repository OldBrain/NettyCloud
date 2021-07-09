package cloudclient.service.impl.command;

import cloudclient.service.CallBackService;
import cloudclient.service.CommandService;
import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Команда подтверждения успешного подключения к серверу
 */
public class LoginOkCommand implements CommandService {
  private CallBackService onCommandReceivedCallback;

  public LoginOkCommand(CallBackService onCommandReceivedCallback) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
  }

  @Override
  public Command processCommand(Command command, SocketChannel channel, ChannelPipeline pipeline) {
    onCommandReceivedCallback.callBack(command);
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.LOGIN_OK;
  }
}
