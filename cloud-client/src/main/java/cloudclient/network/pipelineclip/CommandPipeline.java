package cloudclient.network.pipelineclip;

import cloudclient.service.CallBackService;
import cloudclient.handler.ClientObjectHandler;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class CommandPipeline implements InitializedPipeline {
  @Override
  public ChannelPipeline reloadClip(SocketChannel channel, Command command, CallBackService onCommandReceivedCallback) {
    ChannelPipeline pipeline = channel.pipeline();
    //* Удаляем хэндлеры для передачи файлов
    if (pipeline.get("chunked") != null) {
      pipeline.remove("chunked");
    }
    if (pipeline.get("file_handler") != null) {
      pipeline.remove("file_handler");
    }

    if (pipeline.get("decoder") == null) {
      pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    }
//
    if (pipeline.get("encoder") == null) {
      pipeline.addLast("encoder", new ObjectEncoder());
    }
    if (pipeline.get("command_handler") == null) {
      pipeline.addLast("command_handler", new ClientObjectHandler(onCommandReceivedCallback, channel));
    }

    return pipeline;
  }


}
