package cloudclient.service;

import domain.commands.Command;
import io.netty.channel.ChannelPipeline;

public interface CommandDictionaryService {
  Command processCommand(Command command, ChannelPipeline pipeline);
}
