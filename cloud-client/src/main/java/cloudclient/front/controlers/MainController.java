package cloudclient.front.controlers;

import cloudclient.front.InitAllTable;
import cloudclient.front.action.MouseAction;
import cloudclient.informationaboutfiles.ClientsFiles;
import cloudclient.informationaboutfiles.ServerFiles;
import cloudclient.service.impl.ClientNetworkServiceImp;
import cloudclient.util.ClientPropertiesUtils;
import domain.commands.Command;
import domain.commands.CommandName;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;


public class MainController implements Initializable {
  public static String dirPath;
  private AuthorizationController authorizationController = new AuthorizationController();
  private ClientNetworkServiceImp clientNetworkServiceImp;
  private ClientPropertiesUtils propertiesUtils = new ClientPropertiesUtils();
  private MouseAction mouseAction;
  private InitAllTable initTable;
  @FXML
  public Label waitProgress;
  public AnchorPane progressPanel;
  public AnchorPane msgPanel;
  public ProgressBar progress;
  public Button sendToServer;
  public Button getFromServer;
  public Button delLocalFile;
  public Button upButton;
  public TextField pathField;
  public TableView<ClientsFiles> filesClientTable;
  public TableView<ServerFiles> filesServerTable;
  public TextField info;
  public ComboBox<String> disksBox;

  private String login;
  private String password;
  private String MAIN_DIR;

  public MainController() {
  }

  public void setController(AuthorizationController controller) {
    this.authorizationController = controller;
    login = authorizationController.getLogon();
    password = authorizationController.getPassword();
  }

  public void initialize(URL location, ResourceBundle resources) {
    clientNetworkServiceImp = new ClientNetworkServiceImp((obj) -> {
      runCommandInInterface(obj);
    });

    MAIN_DIR = propertiesUtils.value("MAIN_DIR");
    CreatingTablesMainWindow();
    setPropertiesWindowElements();
    initComboBox();
  }

  private void setPropertiesWindowElements() {
    sendToServer.setDisable(true);
    getFromServer.setDisable(true);
    mouseAction = new MouseAction(this);
    mouseAction.ClientTableMouseAction();
    mouseAction.ServerTableMouseAction();
    dirPath = pathField.getText() + "\\";
    pathField.textProperty().addListener(((observable, oldValue, newValue) ->
        dirPath = newValue + "\\"
    ));
  }

  private void CreatingTablesMainWindow() {
    initTable = new InitAllTable(this);
    initTable.initializeTableWithTheClientFiles();
    initTable.updateLocalTableView(Paths.get(MAIN_DIR));
  }

  private void initComboBox() {
    disksBox.getItems().clear();
    for (Path p : FileSystems.getDefault().getRootDirectories()) {
      disksBox.getItems().add(p.toString());
    }
    disksBox.getSelectionModel().select(0);
  }

  @FXML
  public void upDir(ActionEvent actionEvent) {
    Path upPath = Paths.get(pathField.getText()).getParent();
    if (upPath != null) {
      initTable.updateLocalTableView(upPath);
      info.clear();
      sendToServer.setStyle("-fx-background-color: #FFFFFF;");
      sendToServer.setDisable(true);
    }
  }

  @FXML
  public void sendFilesToServer(ActionEvent actionEvent) {
    if (info.getText().isEmpty()) {
      return;
    }
    sendToServer.setDisable(true);
    getFromServer.setDisable(true);
    progressBarShow();
    try {

      clientNetworkServiceImp.sendCommandToServer(new Command(CommandName.TAKE_FILE_FROM_SERVER,
          new String[]{info.getText(), String.valueOf(Files.size(Paths.get(info.getText()))), ""},
          null));
    } catch (Exception ioException) {
      ioException.printStackTrace();
    }
    info.clear();
  }

  private void progressBarShow() {
    progress.setVisible(true);
    progressPanel.setVisible(true);
    progressPanel.setDisable(true);
    msgPanel.setDisable(true);
    waitProgress.setVisible(true);
  }

  private void requestFileTreeFromServer() {
    clientNetworkServiceImp.sendCommandToServer(new Command(CommandName.GIVE_TREE, null, null));
  }

  @FXML
  public void exitAndClose(ActionEvent actionEvent) {
    if (clientNetworkServiceImp != null && clientNetworkServiceImp.channel.isOpen()) {
      clientNetworkServiceImp.channel.close();
    }
    Platform.exit();
  }

  public void initServerTableView(Command command) {
    if (filesServerTable.getColumns().isEmpty()) {
      initTable.setServerTableColumn();
    }
    filesServerTable.getItems().clear();
    initTable.initDataServerTableColumn(command);
  }

  @FXML
  public void selectDisk(ActionEvent actionEvent) {
    ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
    initTable.updateLocalTableView(Paths.get(element.getSelectionModel().getSelectedItem()));
    info.clear();
    sendToServer.setStyle("-fx-background-color: #FFFFFF;");
    sendToServer.setDisable(true);
  }

  @FXML
  public void deleteLocalFile(ActionEvent actionEvent) {
    File file = new File(info.getText());
    if (file.isFile()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setContentText("Удалить файл! " + info.getText());
      alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.NO);

      if (alert.showAndWait().get() == ButtonType.OK) {

        if (file.delete()) {
          Alert alert1 = new Alert(Alert.AlertType.INFORMATION,
              "Файл успешно удален! ",
              ButtonType.OK);
          alert1.showAndWait().get();

          info.clear();
          initTable.updateLocalTableView(Paths.get(pathField.getText()));
        }
      } else {
      }
    } else {
      Alert alert = new Alert(Alert.AlertType.WARNING,
          "Не удалось удалить файл",
          ButtonType.OK);
      alert.showAndWait();
    }

  }

  @FXML
  public void getFilesFromServer(ActionEvent actionEvent) {
    if (!info.getText().isEmpty()) {
      clientNetworkServiceImp.sendCommandToServer(new Command(
          CommandName.GIVE_MI_FILE,
          new String[]{info.getText(), mouseAction.getSize()},
          null));
    }
  }

  public void runCommandInInterface(Object obj) {
    if (obj instanceof Command) {
      Command command = (Command) obj;
      if (command.commandName == CommandName.BEGIN_FILE_SAVE) {
        progress.setVisible(true);
        progressPanel.setVisible(true);
        progressPanel.setDisable(true);
        msgPanel.setDisable(true);
        waitProgress.setVisible(true);
      }

      if (command.commandName == CommandName.SERVER_FILE_ACCEPTED) {
        progressBarHide();
      }

      if (command.commandName == CommandName.TAKE_TREE) {
        initServerTableView(command);
      }
      if (command.commandName == CommandName.CONNECT_OK) {
        clientNetworkServiceImp.sendCommandToServer(new Command(CommandName.LOGIN,
            new String[]{login, password}));
      }
      if (command.commandName == CommandName.LOGIN_OK) {
        requestFileTreeFromServer();
      }
    }
    if (obj instanceof String) {
      String command = (String) obj;
      if (command == "UPDATE_FILE_TABLE") {
        initTable.updateLocalTableView(Paths.get(pathField.getText()));
      }

    }

  }

  private void progressBarHide() {
    msgPanel.setDisable(false);
    progress.setVisible(false);
    progressPanel.setDisable(true);
    progress.setVisible(false);
    waitProgress.setVisible(false);
  }

  public void removeFileFromServer(ActionEvent actionEvent) {
    clientNetworkServiceImp.sendCommandToServer(new Command(CommandName.DELETE_FILE,
        new String[]{info.getText(), mouseAction.getSize()}
    ));
  }
}