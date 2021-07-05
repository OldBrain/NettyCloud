package server.network.pipelineclip;

import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import server.handler.ServerObjectHandler;

public class CommandPipeline implements InitPipeline {
  public CommandPipeline() {
  }

  @Override
  public ChannelPipeline reloadClip(SocketChannel channel, Command command) {
    ChannelPipeline p = channel.pipeline();
    //* Удаляем хэндлеры для передачи файлов
    if (p.get("chunked")!=null) {
       p.remove("chunked");
    }
    if (p.get("file_handler")!=null) {
      p.remove("file_handler");
    }

    if (p.get("decoder")==null) {
    p.addLast("decoder",new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    }
    if (p.get("encoder")==null) {
      p.addLast("encoder",new  ObjectEncoder());
    }
    if (p.get("command_handler")==null) {
      p.addLast("command_handler", new ServerObjectHandler(channel));

    }

    return p;
  }


}
