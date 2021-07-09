package server.service.command;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.FileList;
import server.StartServer;
import server.service.ServerCommandService;

import java.io.File;

public class DeleteFileFromServerCommand implements ServerCommandService {
  private SocketChannel channel;

  public DeleteFileFromServerCommand(SocketChannel channel) {
    this.channel = channel;
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    File file = new File(command.commandArguments[0]);
    if (file.delete()) {
      sendConfirmationToClient(channel, command);
      sendFileTreeToClient(channel);
    } else {
      sendRebuttalToClient(channel, command);
    }
    return null;
  }

  private void sendRebuttalToClient(SocketChannel channel, Command command) {
    channel.writeAndFlush(new Command(CommandName.SERVER_FILE_DELETE_NO,
        command.commandArguments));
  }

  private void sendConfirmationToClient(SocketChannel channel, Command command) {
    channel.writeAndFlush(new Command(CommandName.SERVER_FILE_DELETE_OK,
        command.commandArguments));
  }

  @Override
  public CommandName getCommand() {
    return CommandName.DELETE_FILE;
  }

  public void sendFileTreeToClient(SocketChannel channel) {
    FileList fileList = new FileList(StartServer.MAIN_DIR);
    channel.writeAndFlush(new Command(CommandName.TAKE_TREE, null, fileList.getFileInfo()));
  }
}
