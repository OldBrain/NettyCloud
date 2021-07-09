package server.service.command;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.PipelineForInFiles;
import server.service.ServerCommandService;
import server.util.SrvPropertiesUtils;

import java.io.File;

public class ReceivingAndWritingFileFromClientCommand implements ServerCommandService {
  private SocketChannel channel;
  private InitPipeline filesPipeline;
  private SrvPropertiesUtils prop;

  public ReceivingAndWritingFileFromClientCommand(SocketChannel channel) {
    this.channel = channel;
    prop = new SrvPropertiesUtils();
    filesPipeline = new PipelineForInFiles();
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    deleteFileIfItExists(command);
    pipeline = filesPipeline.reloadClip(channel, command);
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.TAKE_FILE_FROM_SERVER;
  }

  private void deleteFileIfItExists(Command command) {
    /**Если файл существует то будет перезаписан*/
    String fileP = prop.value("MAIN_DIR") + "\\" + command.commandArguments[0];
    File file = new File(fileP);
    if (file.delete()) {
      System.out.println("Файл будет перезаписан на сервере");
    }
    }
  }

