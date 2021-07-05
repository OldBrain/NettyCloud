package cloudclient.front;

import cloudclient.front.controlers.MainController;
import cloudclient.informationaboutfiles.ClientsFiles;
import cloudclient.informationaboutfiles.ServerFiles;
import domain.commands.Command;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

///* Класс для отрисовки и заполнения таблиц содердащих
// древа файлов как на сервере так и локально.
//

public class InitAllTable {
  private MainController mainController;

  public InitAllTable(MainController mainController) {
    this.mainController = mainController;
  }

  public void initDataServerTableColumn(Command command) {
    for (int i = 0; i < command.commandFileInfo.size(); i++) {
      String name = command.commandFileInfo.get(i)[0];
      String size = command.commandFileInfo.get(i)[1];
      String data = command.commandFileInfo.get(i)[2];
      String fullPath = command.commandFileInfo.get(i)[3];
      ServerFiles serverFile = new ServerFiles(name, size, data, fullPath);
      mainController.filesServerTable.getItems().addAll(serverFile);
    }
  }

  public void setServerTableColumn() {
    TableColumn<ServerFiles, String> columnName = new TableColumn<>("Имя файла");
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    mainController.filesServerTable.getColumns().addAll(columnName);

    TableColumn<ServerFiles, String> columnSize = new TableColumn<>("Размер");
    columnSize.setCellValueFactory(new PropertyValueFactory<>("size"));
    mainController.filesServerTable.getColumns().addAll(columnSize);

    TableColumn<ServerFiles, String> columnDate = new TableColumn<>("Дата");
    columnDate.setCellValueFactory(new PropertyValueFactory<>("data"));
    mainController.filesServerTable.getColumns().add(columnDate);
  }

  public void initializeTableWithTheClientFiles() {
    TableColumn<ClientsFiles, String> fileTypeColumn = new TableColumn<>("T");
    fileTypeColumn.setCellValueFactory(param ->
        new SimpleStringProperty(param.getValue().getType().getName()));
    fileTypeColumn.setPrefWidth(15);

    TableColumn<ClientsFiles, String> fileNameColumn = new TableColumn<>("Имя");
    fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
    fileNameColumn.setPrefWidth(185);

    TableColumn<ClientsFiles, Long> fileSizeColumn = new TableColumn<>("размер");
    fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
        param.getValue().getSize()
    ));
    fileSizeColumn.setPrefWidth(110);

    fileSizeColumn.setCellFactory(column -> {
      return new TableCell<ClientsFiles, Long>() {
        @Override
        protected void updateItem(Long item, boolean empty) {
          if (item == null || empty) {
            setText(null);
            setStyle("");
          } else {
            String text = String.format("%,d bytes", item);
            if (item == -1L) {
              text = "[DIR]";
            }
            setText(text);
          }

        }
      };
    });

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss");
    TableColumn<ClientsFiles, String> fileDateColumn = new TableColumn<>("дата");
    fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(
        param.getValue().getLastModify().format(dtf)));
    fileDateColumn.setPrefWidth(130);

    mainController.filesClientTable.getColumns().addAll(
        fileTypeColumn,
        fileNameColumn,
        fileSizeColumn,
        fileDateColumn);
    mainController.filesClientTable.getSortOrder().add(fileTypeColumn);
  }

  public void updateLocalTableView(Path path) {
    try {
      // Отображаем путь в верхнем TextView
      mainController.pathField.setText(path.normalize().toAbsolutePath().toString());
      mainController.filesClientTable.getItems().clear();
      mainController.filesClientTable.getItems().addAll(Files.list(path).map(ClientsFiles::new).collect(Collectors.toList()));
      mainController.filesClientTable.sort();

    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.WARNING,
          "Не удалось получить список локальных файлов",
          ButtonType.OK);
      alert.showAndWait();

    }
  }
}
