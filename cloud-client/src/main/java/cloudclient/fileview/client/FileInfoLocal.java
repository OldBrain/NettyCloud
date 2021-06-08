package cloudclient.fileview.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
public class FileInfoLocal  {
  public enum FileType {
    FILE("F"), DIRECTORY("D");
    private String name;

    public String getName() {
      return name;
    }

    FileType(String name) {
      this.name = name;
    }
  }

  private String fileName;
  private FileType type;
  private Long size;
  private Long realSize;
  private LocalDateTime lastModify;
  private String fullPath;
//  private String checkSum;



  public String getFileName() {
    return fileName;
  }

  public void setFileName(String name) {
    this.fileName = name;
  }

  public FileType getType() {
    return type;
  }

  public void setType(FileType type) {
    this.type = type;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public LocalDateTime getLastModify() {
    return lastModify;
  }

  public void setLastModify(LocalDateTime lastModify) {
    this.lastModify = lastModify;
  }

  public String getFullPath() {
    return fullPath;
  }

  public FileInfoLocal(Path path) {
    File file = new File(path.toString());
    try {
      this.fileName = path.getFileName().toString();
      this.fullPath = path.toAbsolutePath().toString();
      this.size = Files.size(path);
//      this.realSize = file.length();
//      System.out.println(this.realSize-this.size);

      this.type = Files.isDirectory(path) ?
          FileType.DIRECTORY : FileType.FILE;
      if (this.type == FileType.DIRECTORY) {
        this.size = -1L;
      }
      this.lastModify = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
    } catch (IOException io) {
      throw new RuntimeException("Ошибка файла!");
    }
  }


}
