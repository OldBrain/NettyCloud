package server.factory;

import io.netty.channel.socket.SocketChannel;
import server.service.command.*;
import server.service.impl.ServerCommandDictionaryServiceImpl;
import server.service.ServerCommandDictionaryService;
import server.service.ServerCommandService;

import java.util.ArrayList;
import java.util.List;

public class Factory {
  private static SocketChannel channel;

  public Factory(SocketChannel channel) {
    this.channel = channel;
  }

  public ServerCommandDictionaryService getCommandDictionaryService() {
    return new ServerCommandDictionaryServiceImpl(channel);
  }

  public List<ServerCommandService> getCommandServices() {
    List<ServerCommandService> list = new ArrayList<>();
    list.add(new SendFileTreeToClientCommand(channel));
    list.add(new ReceivingAndWritingFileFromClientCommand(channel));
    list.add(new FileTransferRequestCommand(channel));
    list.add(new DeleteFileFromServerCommand(channel));
    list.add(new NotifyClientAuthorizationIsSuccessfulCommand(channel));
    return list;
  }

}
