package org.example.fintrackapps.uiController;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class EditKategoriPageController {
    UserData userData = UserData.getInstance();
    Session session = Session.getInstance();
    MethodCollection method = new MethodCollection();
    CategoryTable categoryTable = CategoryTable.getInstance();



    @FXML private TextField namaKategori;
    @FXML private TextField limit;
    @FXML private ColorPicker colorPicker;
    @FXML private CheckBox dailyLimitCheckBox;

    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }
    public EditKategoriPageController() throws SQLException {}


    @FXML
    private void initialize() throws SQLException {
        limit.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.startsWith("Rp. ")) {
                limit.setText("Rp. " + newVal.replace("Rp. ", ""));
            }});
        dailyLimitCheckBox.setOnAction(event -> {
            if (!dailyLimitCheckBox.isSelected()) {
                limit.setEditable(false);
                limit.clear(); // or inputField.setText("");
            }else{
                limit.setEditable(true);
            }
        });
        setupValue();

        if (!dailyLimitCheckBox.isSelected()){
            limit.setEditable(false);
        }
    }

    @FXML
    private void setupValue() throws SQLException {
        String kategori = session.getClickedDataKategori()[0].toString();
        Double harga = Double.parseDouble( session.getClickedDataKategori()[1].toString());
        String color = session.getClickedDataKategori()[2].toString();

        namaKategori.setText(kategori);
        if (harga < 0){
            limit.setText("-");
            dailyLimitCheckBox.setSelected(false);
        }else{
            dailyLimitCheckBox.setSelected(true);
            limit.setText(String.valueOf(harga));
        }
        colorPicker.setValue(Color.web(color));
    }

    @FXML
    private void editKategori() throws SQLException {
        categoryTable.editKategori(namaKategori.getText(), Double.parseDouble(limit.getText().split(" ")[1]),colorPicker.getValue().toString());
        session.unsetClickedDataKategori();
        mainPageController.removePopUp();
    }

    @FXML
    private void deleteKategori() throws SQLException {
        categoryTable.deleteKategori();
        session.unsetClickedDataKategori();
        mainPageController.removePopUp();
//        mainPageController.showCategory();
    }
}
