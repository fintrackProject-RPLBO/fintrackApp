package org.example.fintrackapps.uiController;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.*;
import de.jensd.fx.glyphs.fontawesome.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class MainPageController {
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();
    Session session = Session.getInstance();
    LogManager logManager = LogManager.getInstance();
    MethodCollection method = new MethodCollection();

    String currentChart = "pie";

    @FXML private TableView<Object[]> catatanTV;
    @FXML private TableColumn<Object[], String> kategoriCatatanTC;
    @FXML private TableColumn<Object[], String> hargaTC;
    @FXML private TableColumn<Object[], String> tanggalTC;
    @FXML private TableColumn<Object[], String> deskripsiTC;
    @FXML private TableColumn<Object[], String> userTC;
    @FXML private TableColumn<Object[], String> dateUpdateTC;

    @FXML private VBox popupContainer;
    @FXML private VBox graphContainer;
    @FXML private Pane mainPane;

    @FXML private GridPane categoryField;

    @FXML private Label totalLabel;
    @FXML private Label totalPengeluaran;
    @FXML private Label dateLabel;
    @FXML private MenuItem profiles;
    Object[] clickedData;

    private ContainerController containerController;

    public MainPageController() throws SQLException {
    }

    public void setContainerController(ContainerController controller) {
        this.containerController = controller;
    }

    public void initialize() throws SQLException {
        styleTableWithClickableRows(catatanTV);
        popupContainer.setOnMouseClicked(event -> {
            if (event.getTarget() == popupContainer) {
                try {
                    removePopUp();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        mainPane.setOnMouseClicked(event -> {
            if (event.getTarget() == mainPane) {
                styleTableWithClickableRows(catatanTV);
            }
        });


        catatanTV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // or == 2 for double click
                Object[] selected = catatanTV.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    clickedData = selected;
                    for(Object i : clickedData){
                        System.out.println(i.toString());
                    }
                }
            }
        });
        switchGraph();
        refreshTable();
        showCategory();
        calculateSpending();
    }

    @FXML
    public void logoutBtn() throws SQLException {
        if (session.getUsername() == null){
            method.confirmationAlert("Anda Belum Login!");
        }else{
            session.clearSession();
            containerController.removeForm();
            containerController.ShowLoginForm();
        }
    }


    @FXML
    public void showLoginForm() throws SQLException {
        session.clearSession();
        containerController.ShowLoginForm();
    }

    @FXML
    private void showAddCatatanForm() throws SQLException {
        refreshTable();
        if (categoryTable.getKategoriLength() < 1){
            method.confirmationAlert("Anda Belum Memiliki Kategori, Tambahkan Kategori Minimal 1!");
        }else{
            showPopup("/org/example/fintrackapps/AddCatatanPage.fxml");
        }
    }
    @FXML
    private void showEditCatatanForm(){
        if (clickedData == null){
            method.confirmationAlert("Anda Belum Memilih Data Yang Ingin di Edit");
        }else {
            session.setClickedDataCatatan(clickedData);
            showPopup("/org/example/fintrackapps/EditCatatanPage.fxml");
        }
    }


    @FXML
    private void showAddKategoriForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/AddCategoryPage.fxml");
    }
    @FXML
    private void showEditKategoriForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/EditKategoriPage.fxml");
    }

    @FXML
    private void showAddJumlahUangForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/AddJumlahUang.fxml");
    }
    @FXML
    private void showEditJumlahUangForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/EditJumlahUang.fxml");
    }

    @FXML
    private void showChangePasswordForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/ChangePasswordPage.fxml");
    }
    @FXML
    private void showChangeUsernameForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/ChangeUsername.fxml");
    }
    @FXML
    private void showDeleteAccountForm() throws SQLException {
        refreshTable();
        showPopup("/org/example/fintrackapps/DeleteAccountPage.fxml");
    }

    @FXML
    private void showLogManager() throws SQLException {
        refreshTable();
        if (logManager.getAllDataCatatan().size() > 0){
            showPopup("/org/example/fintrackapps/LogViewerManager.fxml");
        }else{
            method.confirmationAlert("Anda Belum Memiliki Log Apapun");
        }
    }



    @FXML
    private void calculateSpending() throws SQLException {
        Double sisa = jumlahUangUser.getJumlahUang();

        totalLabel.setText("Rp. "+method.idrFormat(sisa));
        totalPengeluaran.setText("Total Pengeluaran: Rp."+method.idrFormat(catatanKeuanganTable.countingTotalSpend()));
    }

    @FXML
    private void showPopup(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node popupContent = loader.load();
            Object controller = loader.getController();

            try {
                controller.getClass()
                        .getMethod("setMainPageController", MainPageController.class)
                        .invoke(controller, this);
            } catch (NoSuchMethodException e) {
                // The method doesn't exist - ignore or log if needed
                System.out.println("No setMainPageController method in: " + controller.getClass().getName());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            popupContainer.getChildren().setAll(popupContent);
            popupContainer.setVisible(true);
            popupContainer.toFront();
            popupContainer.setPrefWidth(1044);
            popupContainer.setPrefHeight(550);

            popupContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePopUp() throws SQLException {
        popupContainer.setVisible(false);
        popupContainer.toBack();
        popupContainer.setPrefWidth(0);
        popupContainer.setPrefHeight(0);

        session.unsetClickedDataCatatan();
        session.unsetClickedDataKategori();
        session.unsetLogSession();
        refreshTable();

    }

    public void switchGraph() throws SQLException {
        refreshTable();
        graphContainer.getChildren().clear();

        if (currentChart.equals("pie")){
            graphContainer.getChildren().addAll(getBarChart());
        }
        else{
            graphContainer.getChildren().addAll(getPieChart());
        }
    }

    PieChart getPieChart() throws SQLException {
        currentChart = "pie";

        ArrayList<Object[]> rawData = catatanKeuanganTable.getAllDataCatatan();
        ArrayList<Object[]> rawDataCategory = categoryTable.getAllDataKategori();
        ArrayList<Object[]> pieColor = new ArrayList<>();
        ArrayList<String> allCategory = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<Integer> freqCategory = new ArrayList<>();

        for (Object[] i : rawData){
            String kategori =  i[0].toString();
            if (!allCategory.contains(kategori) && !i[5].toString().contains("surplus")){
                allCategory.add(kategori);
            }
            temp.add(kategori);
        }

        for (Object[] i: rawDataCategory){
            Object[] data = {i[0],method.toCssColor(i[3].toString())};
            if (!pieColor.contains(data)){
                pieColor.add(data);
            }
        }

        for (String i: allCategory){
            freqCategory.add(Collections.frequency(temp,i));
        }

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Category Distribution");
        pieChart.setLegendVisible(false);

        for (int i = 0; i < allCategory.size();i++){
            PieChart.Data data = new PieChart.Data(allCategory.get(i), freqCategory.get(i));
            String color = getPieColor(pieColor,allCategory,i);

            pieChart.getData().add(data);
            Platform.runLater(() -> {data.getNode().setStyle(color);});
        }
        return pieChart;
    }

    String getPieColor(ArrayList<Object[]> pieColor,ArrayList<String> allCategory,Integer i){
        for (Object[] j : pieColor) {
            if (allCategory.get(i).equals(j[0])) {
                return "-fx-pie-color: " + j[1] + ";";
            }
        }
        return "-fx-pie-color: black;";
    }

    String getBarColor(String kategori) throws SQLException {
        if (categoryTable.getColor(kategori) != null){
            return method.toCssColor(categoryTable.getColor(kategori));
        }
        return "Black";
    }

    BarChart<String, Number> getBarChart() throws SQLException {
        // === Bar Chart ===
        currentChart = "bar";

        ArrayList<Object[]> rawData = catatanKeuanganTable.getAllDataCatatan();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> color = new ArrayList<>();
        ArrayList<Double> totalSpendingCategory = new ArrayList<>();

        for (Object[] i : rawData) {
            if (!category.contains(i[0].toString()) && !i[5].toString().contains("surplus")) {
                category.add(i[0].toString());
                color.add(getBarColor(i[0].toString()));
            }
        }

        for (String i : category) {
            totalSpendingCategory.add(catatanKeuanganTable.countingTotalSpend(i));
        }

        // === Axes ===
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Category Distribution");
        xAxis.setLabel("Category");
        yAxis.setLabel("Rupiah");

        // === Data Series ===
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        for (int i = 0; i < category.size(); i++) {
            dataSeries.getData().add(new XYChart.Data<>(category.get(i), totalSpendingCategory.get(i)));
        }
        Platform.runLater(() -> {
            for (int i = 0; i < category.size(); i++) {
                Node node = dataSeries.getData().get(i).getNode();
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + color.get(i) + ";");
                }
            }
        });

        // === Add dummy transparent bar if only 1 category ===
        if (category.size() == 1) {
            XYChart.Data<String, Number> dummy = new XYChart.Data<>(" ", 0); // dummy label
            dataSeries.getData().add(dummy);

            // Make dummy transparent when node is created
            dummy.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: transparent;");
                }
            });
        }

        // Optional spacing
        barChart.setCategoryGap(40);
        barChart.setBarGap(10);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);

        barChart.getData().add(dataSeries);
        return barChart;
    }


    void refreshTable() throws SQLException {
        addingDataCatatanToTable();
        logManager.loggingExpiredData();
        clickedData = null;
        styleTableWithClickableRows(catatanTV);
        showCategory();
        refreshDate();
        profiles.setText(session.getUsername());
        calculateSpending();

        if (currentChart.equals("pie")){
            graphContainer.getChildren().clear();
            graphContainer.getChildren().addAll(getPieChart());
        }else{
            graphContainer.getChildren().clear();
            graphContainer.getChildren().addAll(getBarChart());
        }
    }


    @FXML
    public void showCategory() throws SQLException {
        categoryField.getChildren().clear();
        ArrayList<Object[]> rawData = categoryTable.getAllDataKategori();

        Label headerKategori = new Label("Nama Kategori");
        Label headerLimit = new Label("Limit Kategori");

        headerKategori.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        headerLimit.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane.setHalignment(headerKategori, HPos.CENTER);
        GridPane.setValignment(headerKategori, VPos.CENTER);

        GridPane.setHalignment(headerLimit, HPos.CENTER);
        GridPane.setValignment(headerLimit, VPos.CENTER);

        categoryField.add(headerKategori,0,0);
        categoryField.add(headerLimit,1,0);


        for (int i = 0; i < rawData.size(); i++){
            FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
            String namaKategori = rawData.get(i)[0].toString();

            String limitKategori;
            if (Double.parseDouble(rawData.get(i)[1].toString()) < 0){
                limitKategori = "-";
            } else {
                limitKategori = rawData.get(i)[1].toString();
            }
            String color = rawData.get(i)[3].toString();
            String range = categoryTable.getRange(namaKategori);

            Label labelKategori = new Label(namaKategori);
            Rectangle colorBox = method.createRectangle(18.0,17.0,method.toCssColor(color));
            Label labelLimit = new Label(limitKategori);
            Button editButton = new Button();

            labelKategori.setStyle("-fx-font-size: 14px;");
            labelLimit.setStyle("-fx-font-size: 14px;");

            editButton.setGraphic(editIcon);
            editButton.setStyle("-fx-background-color: transparent;");
            editButton.setOnAction(event->{
                session.setClickedDataKategori(new Object[] {namaKategori,limitKategori,color,range});
                try {
                    showEditKategoriForm();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #e0e0e0;"));
            editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: transparent;"));
            editIcon.setSize("20px");

            GridPane.setHalignment(labelKategori, HPos.CENTER);
            GridPane.setValignment(labelKategori, VPos.CENTER);

            GridPane.setHalignment(colorBox, HPos.LEFT);
            GridPane.setValignment(colorBox, VPos.CENTER);

            GridPane.setHalignment(labelLimit, HPos.CENTER);
            GridPane.setValignment(labelLimit, VPos.CENTER);

            GridPane.setHalignment(editButton, HPos.RIGHT);
            GridPane.setValignment(editButton    , VPos.CENTER);

            categoryField.add(colorBox,0,i+1);
            categoryField.add(labelKategori, 0, i+1);
            categoryField.add(labelLimit, 1, i+1);
            categoryField.add(editButton, 1, i+1);
            }
        }



    @FXML
    public void addingDataCatatanToTable() throws SQLException {
        ArrayList<Object[]> rawData = CatatanKeuanganTable.getInstance().getAllDataCatatan();

        for (Object[] i : rawData){
            System.out.print(i[0]+" ");
            System.out.println(i[1]);
        }

        ObservableList<Object[]> table = FXCollections.observableArrayList(rawData);
        catatanTV.setItems(table);


        kategoriCatatanTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0].toString()));
        hargaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1].toString()));
        tanggalTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2].toString()));
        deskripsiTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3].toString()));
        userTC.setCellValueFactory(cellData -> {Object value = cellData.getValue()[4]; return new SimpleStringProperty(value != null ? value.toString() : " ");});
        dateUpdateTC.setCellValueFactory(cellData -> {Object dateTime = cellData.getValue()[5]; return new SimpleStringProperty(dateTime != null ? dateTime.toString() : " ");});

    }


    @FXML
    public void styleTableWithClickableRows(TableView<Object[]> tableView) {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Object[] item, boolean empty) {
                super.updateItem(item, empty);

                // Default style (applied to all rows including empty ones)
                String style = "-fx-background-color: #ebe5c2; -fx-border-color: #f8f3d9; -fx-border-width: 2 0 0 0;";

                if (!empty && item != null) {
                    // Check if the row contains "limit"
                    boolean containsLimit = false;
                    boolean containsSurplus = false;

                    for (Object value : item) {
                        if (value != null && value.toString().toLowerCase().contains("limit")) {
                            containsLimit = true;
                            break;
                        }
                        if (value != null && value.toString().toLowerCase().contains("surplus")) {
                            containsSurplus = true;
                            break;
                        }
                    }

                    if (containsLimit) {
                        style = "-fx-background-color: #f28b82; -fx-border-color: #f8f3d9; -fx-border-width: 2 0 0 0;";
                    }
                    if (containsSurplus) {
                        style = "-fx-background-color: #27f28d; -fx-border-color: #f8f3d9; -fx-border-width: 2 0 0 0;";
                    }

                    if (isSelected()) {
                        style = "-fx-background-color: #a5c8f0; -fx-border-color: #f8f3d9; -fx-border-width: 2 0 0 0;";
                    }
                }

                setStyle(style);
            }
        });

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    void refreshDate() throws SQLException {
        StringBuilder temp = new StringBuilder();
        if (catatanKeuanganTable.getOldestDate() != null){
            temp.append(catatanKeuanganTable.getOldestDate().toString().replace('-','/'));
        }
        if (catatanKeuanganTable.getNewestDate() != null){
            temp.append(" - ");
            temp.append(catatanKeuanganTable.getNewestDate().toString().replace('-','/'));
        }

        dateLabel.setText(temp.toString());
    }




}
