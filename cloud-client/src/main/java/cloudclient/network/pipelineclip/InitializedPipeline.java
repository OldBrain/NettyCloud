package cloudclient.network.pipelineclip;

import cloudclient.service.CallBackService;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public interface InitializedPipeline {

  public ChannelPipeline reloadClip(SocketChannel channel, Command command, CallBackService onCommandReceivedCallback);
}
