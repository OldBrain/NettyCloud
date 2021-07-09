package server.service.command;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedFile;
import server.network.pipelineclip.CommandPipeline;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.PipelineForOutFiles;
import server.service.ServerCommandService;

import java.io.File;
import java.io.IOException;

public class FileTransferRequestCommand implements ServerCommandService {
  private SocketChannel channel;
  private InitPipeline commandPipeline;
  private InitPipeline outFilesPipeline;

  public FileTransferRequestCommand(SocketChannel channel) {
    this.channel = channel;
    outFilesPipeline = new PipelineForOutFiles();
    commandPipeline = new CommandPipeline();
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    String fullPath = command.commandArguments[0];
    creatingCommandToSendFileToClient(command);
    pipeline = commandPipeline.reloadClip(channel, command);
    channel.writeAndFlush(command);
    pipeline = outFilesPipeline.reloadClip(channel, command);
    sendingFileToClient(channel, command, fullPath);
    pipeline = commandPipeline.reloadClip(channel, command);
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.GIVE_MI_FILE;
  }

  private void creatingCommandToSendFileToClient(Command command) {
    /**Меняем командк на TAKE_FILE аргументы прежние
     * */
    command.commandName = CommandName.TAKE_FILE_FROM_SERVER;
    System.out.println("Размер " + command.commandArguments[1] + " байт");
    /**Выделяем имя файла из строки commandArguments*/
    String[] f = command.commandArguments[0].split("\\\\");
    command.commandArguments[0] = f[f.length - 1];
  }

  private void sendingFileToClient(SocketChannel channel, Command command, String fullPath) {
    ChannelFuture future = null;
    try {
      future = channel.writeAndFlush(new ChunkedFile(new File(fullPath)));
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    future.addListener((ChannelFutureListener) channelFuture -> {
      System.out.println("Finish write file: " + command.commandArguments[0]);

    });
  }
}
