package server;

import domain.fileservise.FileList;
import org.flywaydb.core.Flyway;
import server.network.ServerNetwork;
import server.util.SrvProperties;

public class StartServer {
  static SrvProperties prop = new SrvProperties();
  public static final String MAIN_DIR= prop.value("MAIN_DIR");

  public static void main(String[] args) {
    final String jdbcUrj = "jdbc:postgresql://localhost:5435/cloud";
    final String userName = "postgres";
    final String password = "postgrespass";

    Flyway flyway = Flyway.configure().dataSource(jdbcUrj, userName, password).load();
//    flyway.migrate();




    FileList fileList = new FileList(prop.value("MAIN_DIR"));
    ServerNetwork serverNetwork = new ServerNetwork();

    }

}
