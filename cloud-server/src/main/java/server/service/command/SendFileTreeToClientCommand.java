package server.service.command;


import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.informationaboutfiles.FileList;
import server.StartServer;
import server.service.ServerCommandService;

public class SendFileTreeToClientCommand implements ServerCommandService {

  private SocketChannel channel;

  public SendFileTreeToClientCommand(SocketChannel channel) {
    this.channel = channel;
  }

  @Override
  public Command processCommand(Command command, ChannelPipeline pipeline) {
    FileList fileList = new FileList(StartServer.MAIN_DIR);
    channel.writeAndFlush(new Command(CommandName.TAKE_TREE, null, fileList.getFileInfo()));
    return null;
  }

  @Override
  public CommandName getCommand() {
    return CommandName.GIVE_TREE;
  }
}
