package org.example.fintrackapps.uiController;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.LogManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;


public class LogViewer {
    CategoryTable categoryTable = CategoryTable.getInstance();
    Session session = Session.getInstance();
    LogManager logManager = LogManager.getInstance();
    MethodCollection method = new MethodCollection();

    @FXML
    private VBox log;
    @FXML
    private Label dateLabel;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;

    @FXML private GridPane tableContainer;
    @FXML private TableView<Object[]> catatanTV;
    @FXML private TableColumn<Object[], String> kategoriCatatanTC;
    @FXML private TableColumn<Object[], String> hargaTC;
    @FXML private TableColumn<Object[], String> tanggalTC;
    @FXML private TableColumn<Object[], String> deskripsiTC;

    @FXML private Label highestSpendLabel;
    @FXML private Label highestInComeLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalSpendLabel;



    private ContainerController containerController;

    public LogViewer() throws SQLException {
    }

    public void setContainerController(ContainerController controller) {
        this.containerController = controller;
    }

    public void initialize() throws SQLException {
        getBarChart();
        getPieChart();
        addingDataCatatanToTable();
        styleTableWithClickableRows(catatanTV);
        String date = session.getLogSession().get(0)[2].toString();
        dateLabel.setText(method.getMonthAndYear(date));
        setupLabel();
    }

    public void exportLog() throws IOException {
        Stage stage = (Stage) log.getScene().getWindow();
        ArrayList<Object[]> rawData = session.getLogSession();
        double tableHeight = tableContainer.getHeight();
        tableContainer.setPrefHeight((tableHeight + rawData.size())*1.5);
        method.exportNodeToPDF(log,method.choosePdfSaveLocation(stage));
        tableContainer.setPrefHeight(tableHeight);

    }

    @FXML
    public void setupLabel(){
        ArrayList<Object[]> rawData = session.getLogSession();
        Double highestSpend = 0.0;
        Double highestInCome = 0.0;
        Double totalIncome = 0.0;
        Double totalSpend = 0.0;

        for (Object[] i : rawData){
            String updateDate = i[5].toString();
            Double amount = Double.parseDouble(i[1].toString());
            if (updateDate.contains("surplus")){
                if (amount > highestInCome){
                    highestInCome = amount;
                }
                totalIncome += amount;
            }else{
                if (amount > highestSpend){
                    highestSpend = amount;
                }
                totalSpend += amount;
            }
        }

        highestSpendLabel.setText("Pengeluaran Terbesar: "+method.idrFormat(highestSpend));
        highestInComeLabel.setText("PemasukanTerbesar: "+method.idrFormat(highestInCome));
        totalIncomeLabel.setText("Total Pemasukan: "+method.idrFormat(totalIncome));
        totalSpendLabel.setText("Total Pengeluaran: "+method.idrFormat(totalSpend));

   }

    @FXML
    public void addingDataCatatanToTable() throws SQLException {
        ArrayList<Object[]> rawData = session.getLogSession();

        ObservableList<Object[]> table = FXCollections.observableArrayList(rawData);
        catatanTV.setItems(table);

        kategoriCatatanTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0].toString()));
        hargaTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1].toString()));
        tanggalTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2].toString()));
        deskripsiTC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3].toString()));

    }


    void getPieChart() throws SQLException {
        ArrayList<Object[]> rawData = session.getLogSession();
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

        pieChart.setTitle("Category Distribution");
        pieChart.setLegendVisible(false);

        for (int i = 0; i < allCategory.size();i++){
            PieChart.Data data = new PieChart.Data(allCategory.get(i), freqCategory.get(i));
            String color = getPieColor(pieColor,allCategory,i);

            pieChart.getData().add(data);
            Platform.runLater(() -> {data.getNode().setStyle(color);});
        }

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

    void getBarChart() throws SQLException {
        ArrayList<Object[]> rawData = session.getLogSession();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> color = new ArrayList<>();
        ArrayList<Double> totalSpendingCategory = new ArrayList<>();

        for (Object[] i : rawData) {
            if (!category.contains(i[0].toString()) && !i[5].toString().contains("surplus")) {
                category.add(i[0].toString());
                color.add(getBarColor(i[0].toString()));
                totalSpendingCategory.add(logManager.countingTotalSpend(i[0].toString(),method.getMonthAndYear(i[2].toString())));
            }
        }


        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        String title = method.getMonthAndYear(session.getLogSession().get(0)[2].toString());
        barChart.setTitle(title+" Spending");
        xAxis.setLabel("Category");
        yAxis.setLabel("Rupiah");


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

        if (category.size() == 1) {
            XYChart.Data<String, Number> dummy = new XYChart.Data<>(" ", 0); // dummy label
            dataSeries.getData().add(dummy);

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
    }

    @FXML
    public void styleTableWithClickableRows(TableView<Object[]> tableView) {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Object[] item, boolean empty) {
                super.updateItem(item, empty);

                String style = "-fx-border-width: 2 0 0 0;";

                if (!empty && item != null) {
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
                }

                setStyle(style);
            }
        });

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


}
