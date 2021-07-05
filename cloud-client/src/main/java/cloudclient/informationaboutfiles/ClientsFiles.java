package cloudclient.informationaboutfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Этот класс создан для инициализации TableView,
 * отображающей файлы расположенные на локальном диске клиента
 */
public class ClientsFiles {
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
  private LocalDateTime lastModify;
  private String fullPath;

  public ClientsFiles(Path path) {
    this.fileName = fileName(path);
    this.fullPath = fullPath(path);
    this.size = fileSize(path);
    this.type = fileType(path);
    this.lastModify = lastModify(path);
  }

  private LocalDateTime lastModify(Path path) {
    LocalDateTime lm = null;
    try {
      lm = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
    } catch (IOException ioException) {
      throw new RuntimeException("Не могу определить дату модификации файла!");
    }
    return lm;
  }

  private FileType fileType(Path path) {
    FileType type;
    type = Files.isDirectory(path) ?
        FileType.DIRECTORY : FileType.FILE;
    if (this.type == FileType.DIRECTORY) {
      this.size = -1L;
    }
    return type;
  }

  private Long fileSize(Path path) {
    Long size = null;
    try {
      size = Files.size(path);
    } catch (IOException ioException) {
      throw new RuntimeException("Не могу определить размер файла!");
    }
    return size;
  }

  private String fullPath(Path path) {
    return path.toAbsolutePath().toString();
  }

  private String fileName(Path path) {
    return path.getFileName().toString();
  }

  public String getFileName() {
    return fileName;
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

  public String getFullPath() {
    return fullPath;
  }
}
