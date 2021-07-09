package server;

import org.flywaydb.core.Flyway;
import server.network.ServerNetwork;
import server.util.SrvPropertiesUtils;

public class StartServer {
  static SrvPropertiesUtils prop = new SrvPropertiesUtils();
  public static final String MAIN_DIR = prop.value("MAIN_DIR");
  public static final String jdbcUrl = prop.value("jdbcUrl");
  public static final String userName = prop.value("userName");
  public static final String password = prop.value("password");

  public static void main(String[] args) {
    Flyway flyway = Flyway.configure().dataSource(jdbcUrl, userName, password).load();
    flyway.migrate();
    ServerNetwork serverNetwork = new ServerNetwork();
  }

}
