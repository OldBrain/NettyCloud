package cloudclient.interfase;

import cloudclient.fileview.client.FileInfoLocal;
import cloudclient.fileview.server.FileInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

///* Класс для отрисовки и заполнения таблиц содердащих
// древа файлов как на сервере так и локально.
//

public class InitTables {
  private Controller cnt;

  public InitTables(Controller controller) {
    this.cnt = controller;


  }

  public void  initT() {
    initServerTableView();
    initLocalTableView();

  }
  public void updateLocalTableView(Path path) {
    try {
      // Отображаем путь в верхнем TextView
      cnt.pathField.setText(path.normalize().toAbsolutePath().toString());

      cnt.filesClientTable.getItems().clear();
      cnt.filesClientTable.getItems().addAll(Files.list(path).map(FileInfoLocal::new).collect(Collectors.toList()));
      cnt.filesClientTable.sort();

    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.WARNING,
          "Не удалось получить список локальных файлов",
          ButtonType.OK);
      alert.showAndWait();

    }
  }

  private void initServerTableView() {
    TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя файла");
    fileNameColumn.setCellValueFactory(param ->
        new SimpleStringProperty(param.getValue().getName()));
    fileNameColumn.setPrefWidth(150);
//    cnt.filesServerTable.getColumns().add(fileNameColumn);

    TableColumn<FileInfo, String> fileSizeColumn = new TableColumn<>("Размер");
    fileSizeColumn.setCellValueFactory(param ->
        new SimpleStringProperty(param.getValue().getSize()));
    fileSizeColumn.setPrefWidth(100);
//    cnt.filesServerTable.getColumns().add(fileSizeColumn);

    TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата");
    fileDateColumn.setCellValueFactory(param ->
        new SimpleStringProperty(param.getValue().getData()));
    fileDateColumn.setPrefWidth(80);
//    cnt.filesServerTable.getColumns().add(fileDateColumn);
    cnt.filesServerTable.getColumns().addAll(fileNameColumn, fileSizeColumn, fileDateColumn);
    cnt.filesServerTable.getSortOrder().add(fileNameColumn);
  }

  private void initLocalTableView() {
    TableColumn<FileInfoLocal, String> fileTypeColumn = new TableColumn<>("T");
    fileTypeColumn.setCellValueFactory(param ->
        new SimpleStringProperty(param.getValue().getType().getName()));
    fileTypeColumn.setPrefWidth(15);

    TableColumn<FileInfoLocal, String> fileNameColumn = new TableColumn<>("Имя");
    fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
    fileNameColumn.setPrefWidth(185);

    TableColumn<FileInfoLocal, Long> fileSizeColumn = new TableColumn<>("размер");
    fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(
        param.getValue().getSize()
    ));
    fileSizeColumn.setPrefWidth(110);
    fileSizeColumn.setCellFactory(column -> {
      return new TableCell<FileInfoLocal, Long>() {
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
    TableColumn<FileInfoLocal, String> fileDateColumn = new TableColumn<>("дата");
    fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(
        param.getValue().getLastModify().format(dtf)));
    fileDateColumn.setPrefWidth(130);

    cnt.filesClientTable.getColumns().addAll(
        fileTypeColumn,
        fileNameColumn,
        fileSizeColumn,
        fileDateColumn);
    cnt.filesClientTable.getSortOrder().add(fileTypeColumn);
  }


  public void sortTableLocal(TableColumn tableColumn) {
    cnt.filesClientTable.getSortOrder().add(tableColumn);
  }
}