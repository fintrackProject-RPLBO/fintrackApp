package org.example.fintrackapps.uiController;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.quarkus.logging.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.JumlahUangUser;
import org.example.fintrackapps.tableManager.LogManager;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class LogViewerManagerController {
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();
    Session session = Session.getInstance();
    LogManager logManager = LogManager.getInstance();
    MethodCollection method = new MethodCollection();

    @FXML private ScrollPane scrollPaneContainer;



    private ContainerController containerController;

    public LogViewerManagerController() throws SQLException {
    }

    public void setContainerController(ContainerController controller) {
        this.containerController = controller;
    }

    @FXML
    public void initialize() throws SQLException {
        showLog();
    }

    @FXML
    public void showLog() throws SQLException {
        ArrayList<Object[]> rawData = logManager.getAllDataCatatan();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<ArrayList<Object[]>> base = new ArrayList<>();
        System.out.println(rawData);

        for (Object[] i : rawData){
            String dataDate = method.getMonthAndYear(i[2].toString());
            if (!dateList.contains(dataDate)){
                dateList.add(dataDate);
            }
        }

        for (String i : dateList){
            ArrayList<Object[]> temp = new ArrayList<>();
            for (Object[] j : rawData){
                String dataDate = method.getMonthAndYear(j[2].toString());

                if (i.equals(dataDate)){
                    System.out.println(i+"<=========");
                    System.out.println(dataDate+"<=========");
                    temp.add(j);
                }
            }
            base.add(temp);
        }

        System.out.println(dateList);
        for (ArrayList<Object[]> i : base){
            for (Object[] j : i){
                System.out.print(Arrays.toString(j)+",");
            }
            System.out.println();
        }

        GridPane logListContainer = new GridPane();
        logListContainer.setPrefWidth(400);
        logListContainer.setMaxWidth(Double.MAX_VALUE);
        logListContainer.setHgap(10);
        logListContainer.setVgap(10);
        logListContainer.setPadding(new Insets(10));

// Set column widths: 70% for label, 30% for button
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(30);
        logListContainer.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < dateList.size(); i++) {
            Label labelDate = new Label(dateList.get(i));
            Button viewButton = new Button("View");
            ArrayList<Object[]> data = base.get(i);

            labelDate.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            viewButton.setOnAction(event -> {
                session.setLogSession(data);
                try {
                    loadLog();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Optional: align label to center
            GridPane.setHalignment(labelDate, HPos.CENTER);
            GridPane.setValignment(labelDate, VPos.CENTER);

            // Wrap button in HBox for right alignment
            HBox buttonBox = new HBox(viewButton);
            buttonBox.setAlignment(Pos.CENTER);
            logListContainer.add(labelDate, 0, i);
            logListContainer.add(buttonBox, 1, i);
        }

// Set GridPane as scrollable content
        scrollPaneContainer.setFitToWidth(true); // stretch to scrollpane width
        scrollPaneContainer.setContent(logListContainer);




    }

    @FXML
    private void loadLog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/fintrackapps/Log.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("New Window");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        LogViewerManagerController lvmc = new LogViewerManagerController();
        Session.getInstance().setUsername("Nicsap");
        lvmc.showLog();
    }
}
