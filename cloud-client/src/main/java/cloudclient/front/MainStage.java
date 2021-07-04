package cloudclient.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainStage extends Application {
  Stage stage = new Stage();
  AuthorizationController authorizationController;

  public MainStage(AuthorizationController authorizationController) {
    this.authorizationController = authorizationController;
  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/view/cloud.fxml"));
    primaryStage.setTitle("CloudOn");
    primaryStage.setScene(new Scene(root, 720, 420));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void showWindow() throws Exception {
    start(stage);
  }

}
