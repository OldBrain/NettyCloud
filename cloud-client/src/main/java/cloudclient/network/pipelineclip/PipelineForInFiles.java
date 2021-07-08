package cloudclient.network.pipelineclip;

import cloudclient.service.CallBackService;
import cloudclient.handler.ClientFilesWriteHandler;
import cloudclient.front.controlers.MainController;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;


public class PipelineForInFiles implements InitializedPipeline {

  @Override
  public ChannelPipeline reloadClip(SocketChannel channel, Command command, CallBackService onCommandReceivedCallback) {
    ChannelPipeline pipeline = channel.pipeline();
    //* Удаляем хэндлеры для обработки команд
    if (pipeline.get("decoder") != null) {
      pipeline.remove("decoder");
    }
    if (pipeline.get("encoder") != null) {
      pipeline.remove("encoder");
    }
    if (pipeline.get("command_handler") != null) {
      pipeline.remove("command_handler");
    }

    /**
     * Добавляем обработчики для передачи файлов
     */

    if (pipeline.get("chunked") == null) {
      pipeline.addLast("chunked", new ChunkedWriteHandler());
    }
    if (pipeline.get("file_handler") == null) {
      pipeline.addLast("file_handler", new ClientFilesWriteHandler(MainController.dirPath + command.commandArguments[0], channel, command, onCommandReceivedCallback));

    }

    return pipeline;
  }
}
