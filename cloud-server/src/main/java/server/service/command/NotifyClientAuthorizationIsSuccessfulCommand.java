package server.service.command;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.service.ServerCommandService;

public class NotifyClientAuthorizationIsSuccessfulCommand implements ServerCommandService {
  private SocketChannel channel;

  public NotifyClientAuthorizationIsSuccessfulCommand(SocketChannel channel) {
    this.channel = channel;
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    channel.writeAndFlush(new Command(CommandName.LOGIN_OK));
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.LOGIN;
  }
}
