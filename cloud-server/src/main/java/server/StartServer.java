package server;

import domain.fileservise.FileList;
import org.flywaydb.core.Flyway;
import server.network.ServerNetwork;
import server.util.SrvProperties;

public class StartServer {
  static SrvProperties prop = new SrvProperties();
  public static final String MAIN_DIR= prop.value("MAIN_DIR");

  public static void main(String[] args) {
    final String jdbcUrl = prop.value("jdbcUrl");
    final String userName = prop.value("userName");
    final String password = prop.value("password");

    Flyway flyway = Flyway.configure().dataSource(jdbcUrl, userName, password).load();
    flyway.migrate();




//    FileList fileList = new FileList(prop.value("MAIN_DIR"));
    ServerNetwork serverNetwork = new ServerNetwork();

    }

}
