package server.service;

import domain.commands.Command;
import io.netty.channel.ChannelPipeline;

public interface ServerCommandDictionaryService {
  Command processCommand(Command command, ChannelPipeline pipeline);
}
