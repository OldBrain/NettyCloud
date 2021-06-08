package cloudclient.interfase;

import cloudclient.fileview.client.FileInfoLocal;
import cloudclient.fileview.server.FileInfo;
import cloudclient.fileview.server.FileToSend;
import cloudclient.network.Network;
import cloudclient.util.ClientProperties;
import domain.commands.Command;
import domain.commands.ComName;
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



public class Controller implements Initializable {
//  RealFileSize realFileSize = new RealFileSize();
  public static String dirPath;
  FileToSend fs = new FileToSend();
  Network network;
  ClientProperties prop = new ClientProperties();
  MouseAction mouseAction;
  InitTables initTables;


  @FXML
  public Label waitProgress;
  public AnchorPane progressPanel;
  public AnchorPane msgPanel;
  public ProgressBar progress;
  public Button sendToServer;
  public Button getFromServer;
  public Button delLocalFile;
  public Button upButton;
  @FXML
  public TextField pathField;
  @FXML
  private Button buttonConnect;
  @FXML
  private Button buttonExit;

  @FXML
  public TableView<FileInfoLocal> filesClientTable;
  @FXML
  public TableView<FileInfo> filesServerTable;
  @FXML
  protected TextField info;
  @FXML
  public ComboBox<String> disksBox;

  public void initialize(URL location, ResourceBundle resources) {
    final String MAIN_DIR = prop.value("MAIN_DIR");
    //Создаем структуру таблиц главного окна
    initTables = new InitTables(this);
    initTables.initT();

// Задаем корневой путь для отображения локаольных файлов
    initTables.updateLocalTableView(Paths.get(MAIN_DIR));
    initComboBox();
    buttonConnect.setStyle("-fx-background-color: #ff0000; ");
    sendToServer.setDisable(true);
    getFromServer.setDisable(true);

    mouseAction = new MouseAction(this);
    mouseAction.ClientTableMouseAction();
    mouseAction.ServerTableMouseAction();
    dirPath = pathField.getText()+"\\";

    pathField.textProperty().addListener(((observable, oldValue, newValue) ->
//        System.out.println("OldText " + oldValue + "  NewText " + newValue)
        dirPath = newValue+"\\"

    ));
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
      initTables.updateLocalTableView(upPath);
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
    /**Запускаем прогрессбар*/
    progress.setVisible(true);

    progressPanel.setVisible(true);
    progressPanel.setDisable(true);
    msgPanel.setDisable(true);
    waitProgress.setVisible(true);
//    progress.setProgress(0);
//    progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

    /***/
    try {
      network.sendCommandToServer(new Command(ComName.TAKE_FILE_FROM_SERVER,
          new String[]{info.getText(), String.valueOf(Files.size(Paths.get(info.getText()))), ""},
//          new String[]{info.getText(), realFileSize.realSize(info.getText()), ""},
          null));
    } catch (Exception ioException) {
      ioException.printStackTrace();
    }

    info.clear();

  }

  @FXML
  public void connectToServer(ActionEvent actionEvent) {
    buttonConnect.setStyle("-fx-background-color: gray");
    buttonConnect.setText("please wait");
    network = new Network((obj) -> {
      runCommandInInterface(obj);
    });
  }

  private void getTreeFromServer() {
    network.sendCommandToServer(new Command(ComName.GIVE_TREE, null, null));
  }

  private void setConnectOk() {
    buttonConnect.setText("Connect OK");
    buttonConnect.setStyle("-fx-background-color: green");
    buttonConnect.setDisable(true);
  }

  @FXML
  public void exitAndClose(ActionEvent actionEvent) {

    if (network != null && network.channel.isOpen()) {
      network.channel.close();
    }
    Platform.exit();
  }

  public void setServerTree(Command command) {
    if (!filesServerTable.getItems().isEmpty()) {
      filesServerTable.getItems().clear();
    }

    for (int i = 0; i < command.commandFileInfo.size(); i++) {
      String name = command.commandFileInfo.get(i)[0];
      String size = command.commandFileInfo.get(i)[1];
      String date = command.commandFileInfo.get(i)[2];
      String fullPath = command.commandFileInfo.get(i)[3];
      String item = name + " " + size + " " + date + " " + fullPath;
      FileInfo fileInfo = new FileInfo(name, size, date, fullPath);
      filesServerTable.getItems().add(fileInfo);

    }


  }

  @FXML
  public void selectDisk(ActionEvent actionEvent) {

    ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
    initTables.updateLocalTableView(Paths.get(element.getSelectionModel().getSelectedItem()));
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
          initTables.updateLocalTableView(Paths.get(pathField.getText()));
        }
      } else {
//        System.out.println("ОТКАЗ");
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
      network.sendCommandToServer(new Command(
          ComName.GIVE_MI_FILE,
          new String[]{info.getText(), mouseAction.getSize()},
          null));
    }
  }

  private void runCommandInInterface(Object obj) {
    if (obj instanceof Command) {
      Command command = (Command) obj;
      if (command.commandName == ComName.BEGIN_FILE_SAVE) {
        progress.setVisible(true);
        progressPanel.setVisible(true);
        progressPanel.setDisable(true);
        msgPanel.setDisable(true);
        waitProgress.setVisible(true);

      }

      if (command.commandName == ComName.SERVER_FILE_ACCEPTED) {
        /**Останавливаем Прогрессбар*/
        msgPanel.setDisable(false);
        progress.setVisible(false);
        progressPanel.setDisable(true);
        progress.setVisible(false);
        waitProgress.setVisible(false);

      }

      if (command.commandName == ComName.TAKE_TREE) {
        setServerTree(command);
      }

      if (command.commandName == ComName.CONNECT_OK) {
        setConnectOk();
        getTreeFromServer();
      }


    }
    if (obj instanceof String) {
      String command = (String) obj;
      if (command == "UPDATE_FILE_TABLE") {
        initTables.updateLocalTableView(Paths.get(pathField.getText()));
      }

    }

  }

      public void removeFileFromServer (ActionEvent actionEvent) {
    network.sendCommandToServer(new Command(ComName.DELETE_FILE,
        new String[]{info.getText(), mouseAction.getSize()}
        ));
      }






}