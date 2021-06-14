package server.executingcommands;

import domain.commands.ComName;
import domain.commands.Command;
import domain.fileservise.FileList;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.stream.ChunkedFile;
import server.StartServer;
import server.network.pipelineclip.CommandPipeline;
import server.network.pipelineclip.InitPipeline;
import server.network.pipelineclip.PipelineForOutFiles;
import java.io.File;
import java.io.IOException;


public class SendCommandsToClient {

  InitPipeline commandPipeline = new CommandPipeline();
  InitPipeline outFilesPipeline = new PipelineForOutFiles();

  public void getTree(SocketChannel channel) {
    FileList fileList = new FileList(StartServer.MAIN_DIR);
    channel.writeAndFlush(new Command(ComName.TAKE_TREE, null, fileList.getFileInfo()));
  }

  public void serverFileAccepted(SocketChannel channel) {
    channel.writeAndFlush(new Command(ComName.SERVER_FILE_ACCEPTED ));
  }

  public void sendFileToClient(Command command, SocketChannel channel) {
   /**Меняем командк на TAKE_FILE аргументы прежние
    * */
    command.commandName = ComName.TAKE_FILE_FROM_SERVER;
      String fullPath = command.commandArguments[0];
    System.out.println("Имя команды "+command.commandName);
   System.out.println("Дай файл "+command.commandArguments[0]);
      /**Размер передаваемого файла*/

      String size = command.commandArguments[1];
    System.out.println("Размер "+command.commandArguments[1]+" байт");
      /**Выделяем имя файла из строки commandArguments*/
      String[] f = command.commandArguments[0].split("\\\\");

      command.commandArguments[0] = f[f.length-1];

      ChannelPipeline p = channel.pipeline();
/** Устанавливаем набор обработчиков для приема команд*/
      p = commandPipeline.reloadClip(channel,command);

  /**Отправляем соманду клиенту*/
      channel.writeAndFlush(command);
/** Устанавливаем набор обработчиков для передачи файлов*/
      p = outFilesPipeline.reloadClip(channel,command);

/** Отправляем файл клиенту*/
      ChannelFuture future = null;
      try {
        future = channel.writeAndFlush(new ChunkedFile(new File(fullPath)));
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
      future.addListener((ChannelFutureListener) channelFuture -> {
        System.out.println("Finish write file: "+command.commandArguments[0]);

        });
    /** Устанавливаем набор обработчиков для передачи команд*/
    p = commandPipeline.reloadClip(channel,command);



  }


  public void deleteFile(Command command,SocketChannel channel) {
    File file = new File(command.commandArguments[0]);
    if (file.delete()) {
      /**Отправляем подтверждение клиенту*/
      channel.writeAndFlush(new Command(ComName.SERVER_FILE_DELETE_OK,
          command.commandArguments));
      /**Отправляем список файлов клиенту*/
      getTree(channel);
    } else {
      channel.writeAndFlush(new Command(ComName.SERVER_FILE_DELETE_NO,
          command.commandArguments));
    }
  }

  public void loginOK(SocketChannel channel) {
    channel.writeAndFlush(new Command(ComName.LOGIN_OK));
  }
}