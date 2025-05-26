package org.example.fintrackapps.uiController;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddKategoriController {
    @FXML private TextField limit;
    @FXML private TextField namaKategori;
    @FXML private ColorPicker colorPicker;
    @FXML private CheckBox dailyLimitCheckBox;



    UserData userData = UserData.getInstance();
    Session session = Session.getInstance();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    MethodCollection method = new MethodCollection();
    CategoryTable categoryTable = CategoryTable.getInstance();



    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }
    public AddKategoriController() throws SQLException {
    }


    @FXML
    private void initialize() throws SQLException {
        limit.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.startsWith("Rp. ")) {
                char[] str = limit.getText().toCharArray();
                StringBuilder temp = new StringBuilder();

                for (int i = 0; i < str.length; i++){
                    if (method.isNum(String.valueOf(str[i]))){
                        temp.append(str[i]);
                    }
                }
                limit.setText("Rp. "+temp.toString());
            }});
        dailyLimitCheckBox.setOnAction(event -> {
            if (!dailyLimitCheckBox.isSelected()) {
                limit.setEditable(false);
                limit.clear(); // or inputField.setText("");
            }else{
                limit.setEditable(true);
            }
        });
        if (!dailyLimitCheckBox.isSelected()){
            limit.setEditable(false);
        }

    }

    @FXML
    private void addKategori() throws SQLException {
        if(categoryTable.getKategoriLength() <= 6){
            if (dailyLimitCheckBox.isSelected() && method.isThereAnyLetter(limit.getText().split(" ")[1])){
                    method.confirmationAlert("Limit tidak boleh memiliki huruf");
            }
            else if (dailyLimitCheckBox.isSelected() && limit.getText().split(" ")[1].isEmpty()){
                method.confirmationAlert("Limit Dan nama kategori tidak boleh kosong");
            }
            else{
                Double DailyLimit = -1.0;
                if (dailyLimitCheckBox.isSelected()){DailyLimit = Double.parseDouble(limit.getText().split(" ")[1]);}

                String nama = namaKategori.getText().toString();
                String color = colorPicker.getValue().toString();
                if (categoryTable.getAllColor().contains(color)){method.confirmationAlert("Warna Sudah Pernah Diambil, Pilih Warna Yang Belum!");
                }else if (categoryTable.addKategori(DailyLimit,nama,color)){
                    method.confirmationAlert("Kategori Berhasil Di Tambahkan");
                    mainPageController.removePopUp();
                }else {method.confirmationAlert("Kategori Sudah Ada Di Daftar Kategori!");}
            }
        }else{
            method.confirmationAlert("Jumlah Kategori Sudah Mencapai Maksimal!");
        }

    }



}
