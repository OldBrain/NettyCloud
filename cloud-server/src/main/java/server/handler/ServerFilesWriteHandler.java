package server.handler;

import domain.commands.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import server.executingcommands.SendCommandsToClient;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.CommandPipeline;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ServerFilesWriteHandler extends ChannelInboundHandlerAdapter {
  String arg;
  SocketChannel channel;
  Command command;
  ChannelPipeline p;
  InitPipeline commandPipeline = new CommandPipeline();
  SendCommandsToClient sendToClient = new SendCommandsToClient();

  public ServerFilesWriteHandler(String argument, SocketChannel channel, Command command) {
    this.arg = argument;
    this.command = command;
    this.channel = channel;
    ChannelPipeline p = channel.pipeline();
  }


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object partsFile) throws Exception {
    ByteBuf byteBuf = (ByteBuf) partsFile;

    try ( OutputStream os = new BufferedOutputStream(new FileOutputStream(arg, true))) {

      while (byteBuf.isReadable()) {
        os.write(byteBuf.readByte());
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    long size1 = Long.parseLong(command.commandArguments[1]);
    long size2 = Files.size(Paths.get(arg));

/** Очищаем буфер*/
    byteBuf.release();

//  System.out.println("Принимаю файл на сервер>" + command.commandArguments[0] + " " + size1 + "->" + size2 + " bytes");

    /** Если  size2 = size1 то передача файла закончена
     и можно менять pipeline на прием команд */

    if ((size2==size1)) {
      System.out.println("Файл записан на сервер!");
      /**Устанавливаем pipeline в прием команд */
    //  byteBuf.release();
      p = commandPipeline.reloadClip(channel, command);


      /**Отправляем подтверждение клиенту */
      sendToClient.serverFileAccepted(channel);

    }
  }
}