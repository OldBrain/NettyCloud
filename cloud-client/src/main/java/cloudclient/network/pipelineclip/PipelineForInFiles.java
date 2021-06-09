package cloudclient.network.pipelineclip;

import cloudclient.service.CallBackService;
import cloudclient.hendler.ClientFilesWriteHandler;
import cloudclient.front.Controller;
import domain.commands.Command;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;


public class PipelineForInFiles implements InitPipeline{

//  String dataDir = "clientdata";
//  String dataDir = Controller.dirPath;

//String getDataDir=

  @Override
  public ChannelPipeline reloadClip(SocketChannel channel, Command command, CallBackService onCommandReceivedCallback) {
    ChannelPipeline p = channel.pipeline();


    System.out.println("Перед сменой для файлов "+p.names());
    //* Удаляем хэндлеры для обработки команд
    if (p.get("decoder")!=null) {
      p.remove("decoder");
    }
    if (p.get("encoder")!=null) {
      p.remove("encoder");
    }
    if (p.get("command_handler")!=null) {
      p.remove("command_handler");
    }

    /**
     * Добавляем обработчики для передачи файлов
     */

    if (p.get("chunked")==null) {
      p.addLast("chunked",new ChunkedWriteHandler());
    }
    if (p.get("file_handler")==null) {
      p.addLast("file_handler",new ClientFilesWriteHandler(Controller.dirPath+command.commandArguments[0],channel,command,onCommandReceivedCallback));

    }

    return p;
  }
}
