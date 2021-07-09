package server;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class FileList implements Serializable {

  private List<String[]> fileList = new LinkedList<String[]>();

  public FileList(String path) {
    File dir = new File(path);
    File[] fileArr = dir.listFiles();
    for (int i = 0; i < fileArr.length; i++) {
      String[] fileInfo = new String[4];
      fileInfo[0] = fileArr[i].getName();
      fileInfo[1] = String.valueOf(fileArr[i].length()) + " byte";
      fileInfo[2] = getFileData(fileArr[i]);
      fileInfo[3] = fileArr[i].getAbsolutePath();
      fileList.add(fileInfo);
    }
  }

  private String getFileData(File file) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String data = sdf.format(file.lastModified());
    return data;
  }

  public List<String[]> getFileInfo() {
    return fileList;
  }


}
