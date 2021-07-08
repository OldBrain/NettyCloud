package cloudclient.factory;

import cloudclient.service.CallBackService;
import cloudclient.service.CommandDictionaryService;
import cloudclient.service.CommandService;
import cloudclient.service.impl.CommandDictionaryServiceImpl;
import cloudclient.service.impl.command.LoginOkCommand;
import cloudclient.service.impl.command.ReadyToReceiveTheFileFromTheServerCommand;
import cloudclient.service.impl.command.GetFileTreeServerCommand;
import cloudclient.service.impl.command.ServerAcceptedFileCommand;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.List;

public class Factory {
  private CallBackService onCommandReceivedCallback;
  private SocketChannel channel;

  public Factory(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
  }

  public CommandDictionaryService getCommandDictionaryService() {
    return new CommandDictionaryServiceImpl(onCommandReceivedCallback, channel);
  }

  public List<CommandService> getCommandServices() {
    List<CommandService> list = new ArrayList<>();
    list.add(new GetFileTreeServerCommand(onCommandReceivedCallback));
    list.add(new ServerAcceptedFileCommand(onCommandReceivedCallback));
    list.add(new ReadyToReceiveTheFileFromTheServerCommand(onCommandReceivedCallback, channel));
    list.add(new LoginOkCommand(onCommandReceivedCallback));
    return list;
  }

}
