package cloudclient.front;

import cloudclient.service.impl.ClientNetworkServiceImp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable {
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
    openMainWindow();
    closeCurrentWindow();
  }

  private void closeCurrentWindow() {
    Stage stage = (Stage) connectButton.getScene().getWindow();
    stage.close();
  }

  private void openMainWindow() {
    try {
      MainStage mainStage = new MainStage(this);
      mainStage.showWindow();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void exit(ActionEvent actionEvent) {
    if (clientNetworkServiceImp != null && clientNetworkServiceImp.channel.isOpen()) {
      clientNetworkServiceImp.channel.close();
    }
    Platform.exit();
  }
}
