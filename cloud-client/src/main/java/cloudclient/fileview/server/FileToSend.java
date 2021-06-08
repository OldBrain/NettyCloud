package cloudclient.fileview.server;

import cloudclient.util.ClientProperties;
import javafx.stage.FileChooser;
import java.io.File;

public class FileToSend {
  FileChooser fc = new FileChooser();
  ClientProperties prop = new ClientProperties();

  public String GetSelectedFiles() {
    fc.setInitialDirectory(new File(prop.value("MAIN_DIR")));
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      return selectedFile.getAbsolutePath();
    } else {
      return "No files to send";
    }
  }
}
