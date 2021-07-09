package server.handler;

import domain.commands.Command;
import domain.commands.CommandName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.CommandPipeline;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerFilesWriteHandler extends ChannelInboundHandlerAdapter {
  private String arg;
  private SocketChannel channel;
  private Command command;
  private ChannelPipeline pipeline;
  private InitPipeline commandPipeline = new CommandPipeline();

  public ServerFilesWriteHandler(String argument, SocketChannel channel, Command command) {
    this.arg = argument;
    this.command = command;
    this.channel = channel;
    this.pipeline = channel.pipeline();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object partsFile) throws Exception {
    ByteBuf byteBuf = (ByteBuf) partsFile;

    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(arg, true))) {
      while (byteBuf.isReadable()) {
        os.write(byteBuf.readByte());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    long size1 = Long.parseLong(command.commandArguments[1]);
    long size2 = Files.size(Paths.get(arg));
    byteBuf.release();

    if ((size2 == size1)) {
      System.out.println("Файл записан на сервер!");
      /**Устанавливаем pipeline в прием команд */
      pipeline = commandPipeline.reloadClip(channel, command);
      /**Отправляем подтверждение клиенту */
      channel.writeAndFlush(new Command(CommandName.SERVER_FILE_ACCEPTED));
    }
  }
}