package cloudclient.front;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MouseAction {
  MainController cnt;
  String size;

  public MouseAction(MainController cnt) {
    this.cnt = cnt;
  }

  void ClientTableMouseAction() {
    InitTables initTables = new InitTables(cnt);
    cnt.filesClientTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
              Path path = Paths.get(cnt.pathField.getText()).resolve(
                  cnt.filesClientTable.getSelectionModel().getSelectedItem().getFileName()

              );
              if (Files.isDirectory(path)) {

                initTables.updateLocalTableView(path);
              } else {

                cnt.sendToServer.setStyle("-fx-background-color: #008F7A;");
                cnt.sendToServer.setDisable(false);
                cnt.getFromServer.setDisable(true);
                cnt.getFromServer.setStyle("-fx-background-color: #FFFFFF;");

                cnt.info.clear();
                cnt.info.setText(cnt.filesClientTable.getSelectionModel().getSelectedItem().getFullPath());
              }
            }
          }
        }

    );

  }
  void ServerTableMouseAction() {
    cnt.filesServerTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
              cnt.getFromServer.setStyle("-fx-background-color: #008F7A;");
              cnt.getFromServer.setDisable(false);
              cnt.sendToServer.setDisable(true);
              cnt.sendToServer.setStyle("-fx-background-color: #FFFFFF;");
              cnt.info.clear();
              size = cnt.filesServerTable.getSelectionModel().getSelectedItem().getSize().split(" ")[0];
//              String[] sizes = size.split(" ");

              cnt.info.setText(cnt.filesServerTable.getSelectionModel().getSelectedItem().getFullPath());
//                  +"*"+size.split(" ")[0]);
            }
          }
        }
    );
}

  public String getSize() {
    return size;
  }
}