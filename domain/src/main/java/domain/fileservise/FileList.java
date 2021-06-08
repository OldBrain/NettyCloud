package domain.fileservise;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class FileList implements Serializable {

  List<String[]> fileList = new LinkedList<String[]>();
  public FileList(String path) {

    File dir = new File(path);
    File[] fileArr = dir.listFiles();
    for (int i = 0; i < fileArr.length; i++) {
      String[] fileInfo = new String[4];
      fileInfo[0] = fileArr[i].getName();
      fileInfo[1] = String.valueOf(fileArr[i].length()) + " byte";
      fileInfo[2] = getFileData(fileArr[i]);
      fileInfo[3] = fileArr[i].getAbsolutePath();
//      try {
//        fileInfo[4] = checkSum.checksum(fileArr[i].getAbsolutePath());
//      } catch (IOException ioException) {
//        ioException.printStackTrace();
//      } catch (NoSuchAlgorithmException e) {
//        e.printStackTrace();
//      }
      fileList.add(fileInfo);
    }
  }

  private String getFileData(File file) {
//    File[] fileList = file.listFiles();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String data = sdf.format(file.lastModified());
    return data;
  }


  public List<String[]> getFileInfo() {
    return fileList;
  }


}
