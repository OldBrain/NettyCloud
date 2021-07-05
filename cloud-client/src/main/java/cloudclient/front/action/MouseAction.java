package cloudclient.front.action;

import cloudclient.front.InitAllTable;
import cloudclient.front.controlers.MainController;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MouseAction {
  private MainController controller;
  private String size;

  public MouseAction(MainController controller) {
    this.controller = controller;
  }

  public void ClientTableMouseAction() {
    InitAllTable initTables = new InitAllTable(controller);
    controller.filesClientTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
              Path path = Paths.get(controller.pathField.getText()).resolve(
                  controller.filesClientTable.getSelectionModel().getSelectedItem().getFileName()

              );
              if (Files.isDirectory(path)) {

                initTables.updateLocalTableView(path);
              } else {

                controller.sendToServer.setStyle("-fx-background-color: #008F7A;");
                controller.sendToServer.setDisable(false);
                controller.getFromServer.setDisable(true);
                controller.getFromServer.setStyle("-fx-background-color: #FFFFFF;");

                controller.info.clear();
                controller.info.setText(controller.filesClientTable.getSelectionModel().getSelectedItem().getFullPath());
              }
            }
          }
        }
    );
  }

  public void ServerTableMouseAction() {
    controller.filesServerTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            if (event.getClickCount() == 2) {
              controller.getFromServer.setStyle("-fx-background-color: #008F7A;");
              controller.getFromServer.setDisable(false);
              controller.sendToServer.setDisable(true);
              controller.sendToServer.setStyle("-fx-background-color: #FFFFFF;");
              controller.info.clear();
              size = controller.filesServerTable.getSelectionModel().getSelectedItem().getSize().split(" ")[0];
              controller.info.setText(controller.filesServerTable.getSelectionModel().getSelectedItem().getFullPath());
            }
          }
        }
    );
  }

  public String getSize() {
    return size;
  }
}