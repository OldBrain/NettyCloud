package server.service.impl;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.factory.Factory;
import server.service.ServerCommandDictionaryService;
import server.service.ServerCommandService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCommandDictionaryServiceImpl implements ServerCommandDictionaryService {
  private Map<CommandName, ServerCommandService> commandDictionary;
  private Factory factory;
  private SocketChannel channel;

  public ServerCommandDictionaryServiceImpl(SocketChannel channel) {
    this.channel = channel;
    factory = new Factory(channel);
    this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
  }

  private Map<CommandName, ServerCommandService> getCommandDictionary() {
    List<ServerCommandService> commandServiceList = factory.getCommandServices();
    Map<CommandName, ServerCommandService> commandDictionary = new HashMap<>();
    for (ServerCommandService commandService : commandServiceList) {
      commandDictionary.put(commandService.getCommand(), commandService);
    }
    return commandDictionary;
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    if (commandDictionary.containsKey(command.commandName)) {
      return commandDictionary.get(command.commandName).processCommand(command,pipeline);
    }
    return null;
  }
}
