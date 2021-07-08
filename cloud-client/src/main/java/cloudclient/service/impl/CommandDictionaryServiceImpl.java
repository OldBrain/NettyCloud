package cloudclient.service.impl;

import cloudclient.factory.Factory;
import cloudclient.service.CallBackService;
import cloudclient.service.CommandDictionaryService;
import cloudclient.service.CommandService;
import domain.commands.CommandName;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {
  private CallBackService onCommandReceivedCallback;
  private Map<CommandName, CommandService> commandDictionary;
  private Factory factory;
  private SocketChannel channel;

  public CommandDictionaryServiceImpl(CallBackService onCommandReceivedCallback, SocketChannel channel) {
    this.onCommandReceivedCallback = onCommandReceivedCallback;
    this.channel = channel;
    factory = new Factory(onCommandReceivedCallback, channel);
    this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
  }

  private Map<CommandName, CommandService> getCommandDictionary() {
    List<CommandService> commandServiceList = factory.getCommandServices();
    Map<CommandName, CommandService> commandDictionary = new HashMap<>();
    for (CommandService commandService : commandServiceList) {
      commandDictionary.put(commandService.getCommand(), commandService);
    }
    return commandDictionary;
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    if (commandDictionary.containsKey(command.commandName)) {
      return commandDictionary.get(command.commandName).processCommand(command, channel, pipeline);
    }
    return null;
  }
}
