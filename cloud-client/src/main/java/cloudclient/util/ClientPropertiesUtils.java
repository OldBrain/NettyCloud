package cloudclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientPropertiesUtils {
  private String propValue = null;
  private Properties pr = new Properties();

  public String value(String propName) {
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
