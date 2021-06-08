package server.network.pipelineclip;

import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public interface InitPipeline {

  public ChannelPipeline reloadClip(SocketChannel channel, Command command);
}
