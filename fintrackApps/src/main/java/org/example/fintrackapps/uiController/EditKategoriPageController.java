package org.example.fintrackapps.uiController;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CategoryTable;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class EditKategoriPageController {
    private static final Logger log = LoggerFactory.getLogger(EditKategoriPageController.class);
    Session session = Session.getInstance();
    MethodCollection method = new MethodCollection();
    CategoryTable categoryTable = CategoryTable.getInstance();



    @FXML private TextField namaKategori;
    @FXML private TextField limit;
    @FXML private ColorPicker colorPicker;
    @FXML private CheckBox dailyLimitCheckBox;

    @FXML private RadioButton harian;
    @FXML private RadioButton mingguan;
    @FXML private RadioButton bulanan;
    ToggleGroup group = new ToggleGroup();

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
        setupValue();
        dailyLimitCheckBox.setOnAction(event -> {
            if (!dailyLimitCheckBox.isSelected()) {
                limit.setVisible(false);
                limit.setEditable(false);
                limit.clear(); // or inputField.setText("");

                harian.setVisible(false);
                mingguan.setVisible(false);
                bulanan.setVisible(false);

            }else{
                limit.setEditable(true);
                limit.setVisible(true);

                harian.setVisible(true);
                mingguan.setVisible(true);
                bulanan.setVisible(true);
            }
        });
        if (!dailyLimitCheckBox.isSelected()){
            limit.setEditable(false);
            limit.setVisible(false);
            harian.setVisible(false);
            mingguan.setVisible(false);
            bulanan.setVisible(false);
        }


        harian.setToggleGroup(group);
        mingguan.setToggleGroup(group);
        bulanan.setToggleGroup(group);
    }

    @FXML
    private void setupValue() throws SQLException {
        String kategori = session.getClickedDataKategori()[0].toString();
        Double harga = Double.parseDouble( session.getClickedDataKategori()[1].toString());
        String color = session.getClickedDataKategori()[2].toString();
        String range = session.getClickedDataKategori()[3].toString();

        namaKategori.setText(kategori);
        if (harga < 0){
            limit.setText("-");
            dailyLimitCheckBox.setSelected(false);
        }else{
            dailyLimitCheckBox.setSelected(true);
            limit.setText(String.valueOf(harga));
        }
        colorPicker.setValue(Color.web(color));

        if (range.equals("Harian")){
            harian.setSelected(true);
        }else if (range.equals("Mingguan")) {
            mingguan.setSelected(true);
        }else if (range.equals("Bulanan")){
            bulanan.setSelected(true);
        }

    }

    @FXML
    private void editKategori() throws SQLException {
        RadioButton selectedRadio = (RadioButton) group.getSelectedToggle();
        String range = (selectedRadio != null) ? selectedRadio.getText() : "-" ;
        String limitText = (limit.getText().split(" ").length > 1) ? limit.getText().split(" ")[1] : null;
        Double dailyLimit = -1.0;


        if (dailyLimitCheckBox.isSelected()){
            if (limitText == null){
                method.confirmationAlert("limit tidak boleh kosong");
            } else if (method.isThereAnyLetter(limitText) || method.isThereAnySymbol(limitText)){
                method.confirmationAlert("Kolom Batasan Harian Tidak Boleh Memiliki Huruf Atau Simbol");
            }
            else{
                dailyLimit = Double.parseDouble(limit.getText().split(" ")[1]);
                categoryTable.editKategori(namaKategori.getText(), dailyLimit,colorPicker.getValue().toString(),range);
                session.unsetClickedDataKategori();
                mainPageController.removePopUp();
            }
        }else{
            categoryTable.editKategori(namaKategori.getText(), dailyLimit,colorPicker.getValue().toString(),range);
            session.unsetClickedDataKategori();
            mainPageController.removePopUp();
        }


    }

    @FXML
    private void deleteKategori() throws SQLException {
        categoryTable.deleteKategori();
        session.unsetClickedDataKategori();
        mainPageController.removePopUp();
    }
}
