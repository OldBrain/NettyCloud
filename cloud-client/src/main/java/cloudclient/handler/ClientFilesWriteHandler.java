package cloudclient.handler;

import cloudclient.service.CallBackService;
import cloudclient.network.pipelineclip.InitializedPipeline;
import cloudclient.network.pipelineclip.CommandPipeline;
import domain.commands.Command;
import domain.commands.ComName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientFilesWriteHandler extends ChannelInboundHandlerAdapter {
  private String arg;
  private SocketChannel channel;
  private Command command;
  private InitializedPipeline commandPipeline = new CommandPipeline();
  private ChannelPipeline pipeline;
  private CallBackService onCommandReceivedCallback;

  public ClientFilesWriteHandler(String argument, SocketChannel channel, Command command, CallBackService callBackService) {
    this.arg = argument;
    this.command = command;
    this.channel = channel;
    this.onCommandReceivedCallback = callBackService;
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

    onCommandReceivedCallback.callBack(new Command(
        ComName.BEGIN_FILE_SAVE,
        new String[]{String.valueOf(size1), String.valueOf(size2)}));
    /**Очищаем буфер*/
    byteBuf.release();
    System.out.println("Принимаю файл>" + command.commandArguments[0]
        + " " + size1
        + "->" + size2 + " bytes /");

    /** Если size2 = size1 то передача файла закончена
     можно менять pipeline на прием команд и останавливать
     прогрессбар*/
    if (size1 == size2) {
      System.out.println("Файл записан на клтент!");
//      byteBuf.release();
      /**Возвращаем набор обработчиков для обработки команд */
      pipeline = commandPipeline.reloadClip(channel, command, onCommandReceivedCallback);
      /**Отправляем подтверждение серверу */
      channel.writeAndFlush(new Command(ComName.CLIENT_FILE_ACCEPTED));

      /**Останавливаем прогрессбар*/
      onCommandReceivedCallback.callBack(new Command(ComName.SERVER_FILE_ACCEPTED));
/** Обновляем таблицу файлов клиента*/
      onCommandReceivedCallback.callBack("UPDATE_FILE_TABLE");
    }
  }
}