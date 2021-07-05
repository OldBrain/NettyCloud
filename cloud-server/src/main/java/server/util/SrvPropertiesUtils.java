package server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SrvPropertiesUtils {

  public String value(String propName) {
    String propValue = null;
    Properties pr = new Properties();
    InputStream inputStream = this.getClass().
        getResourceAsStream("/config.properties");

    try {
      pr.load(inputStream);
      propValue = pr.getProperty(propName, null);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } finally {
      try {
        inputStream.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
    return propValue;
  }
}
