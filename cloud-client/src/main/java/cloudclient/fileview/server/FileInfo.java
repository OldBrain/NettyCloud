package cloudclient.fileview.server;

public class FileInfo {
  private String name;
  private String size;
  private String data;
  private String fullPath;

  public FileInfo(String name, String size, String data,String fullPath) {
    this.name = name;
    this.size = size;
    this.data = data;
    this.fullPath = fullPath;
  }

  public String getName() {
    return name;
  }

  public String getSize() {
    return size;
  }

  public String getData() {
    return data;
  }

  public String getFullPath() {
    return fullPath;
  }
}
