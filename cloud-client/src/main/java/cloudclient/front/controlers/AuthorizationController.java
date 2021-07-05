package cloudclient.front.controlers;

import cloudclient.service.impl.ClientNetworkServiceImp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable {

  private Stage mainStage;
  private MainController mainController;

  @FXML
  public Image img;
  @FXML
  public TextField logonField;
  @FXML
  public TextField passwordFields;
  public Button connectButton;

  ClientNetworkServiceImp clientNetworkServiceImp;

  public void initialize(URL location, ResourceBundle resources) {
    connectButton.setDisable(false);
    logonField.setText("testlogin");
    passwordFields.setText("password");
  }

  public String getLogon() {
    return logonField.getText();
  }

  public String getPassword() {
    return passwordFields.getText();
  }

  public void connectionButtonIsPressed(ActionEvent actionEvent) {
    createMainWindow();
    closeCurrentWindow();
    mainStage.show();
  }

  private void closeCurrentWindow() {
    Stage stage = (Stage) connectButton.getScene().getWindow();
    stage.close();
  }


  private void createMainWindow() {
    try {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cloud.fxml"));
      mainStage = new Stage();
      Parent root = fxmlLoader.load();
      mainStage.setTitle("CloudOn");
      mainStage.setScene(new Scene(root, 720, 420));
      mainController = fxmlLoader.getController();
      mainController.setController(this);
      ((MainController) fxmlLoader.getController()).setController(this);
//      mainStage.initModality(Modality.APPLICATION_MODAL);
//      mainStage.initStyle(StageStyle.UTILITY);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

  }


  public void exit(ActionEvent actionEvent) {
    if (clientNetworkServiceImp != null && clientNetworkServiceImp.channel.isOpen()) {
      clientNetworkServiceImp.channel.close();
    }
    Platform.exit();
  }
}
