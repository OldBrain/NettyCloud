package cloudclient.network.pipelineclip;

import cloudclient.executingcommands.CallBack;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public interface InitPipeline {

  public ChannelPipeline reloadClip(SocketChannel channel, Command command, CallBack onCommandReceivedCallback);
}
