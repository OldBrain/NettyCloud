package cloudclient.service;

import domain.commands.CommandName;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public interface CommandService {
  Command processCommand(Command command, SocketChannel channel, ChannelPipeline pipeline);

  CommandName getCommand();
}
