package server.service;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;

public interface ServerCommandService {
  Command processCommand(Command command,ChannelPipeline pipeline);

  CommandName getCommand();
}
