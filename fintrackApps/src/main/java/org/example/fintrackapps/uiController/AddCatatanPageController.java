package org.example.fintrackapps.uiController;

import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.JumlahUangUser;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AddCatatanPageController {
    MethodCollection method = new MethodCollection();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }

    @FXML private ComboBox<String> category;
    @FXML private TextField priceField;
    @FXML private DatePicker date;
    @FXML private TextField descriptionField;


    public AddCatatanPageController() throws SQLException {
    }

    @FXML
    private void initialize() throws SQLException {
        ArrayList<Object[]> data = categoryTable.getAllDataKategori();
        date.setValue(LocalDate.now());
        for (Object[] i : data){
            category.getItems().add(i[0].toString());
        }

        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.startsWith("Rp. ")) {
                char[] str = priceField.getText().toCharArray();
                StringBuilder temp = new StringBuilder();

                for (int i = 0; i < str.length; i++){
                    if (method.isNum(String.valueOf(str[i]))){
                        temp.append(str[i]);
                    }
                }

                priceField.setText("Rp. "+temp.toString());
                }});

    }

    @FXML
    private void addCatatan() throws SQLException, IOException {
        if (category.getValue() == null){
            method.confirmationAlert("Kategori tidak boleh kosong");
        }
        else if (priceField.getText().isEmpty()){
            method.confirmationAlert("harga tidak boleh kosong");
        }
        else if (method.isThereAnyLetter(priceField.getText().split(" ")[1])){
            method.confirmationAlert("harga tidak boleh memiliki huruf");
        }
        else if(date.getValue() == null){
            method.confirmationAlert("tanggal tidak boleh kosong");
        }
        else if (!(category.getValue() == null && priceField.getText().isEmpty() && date.getValue() == null)){
            String kategori = category.getValue();
            Double harga = Double.parseDouble(priceField.getText().split(" ")[1]);
            String tanggal = date.getValue().toString();
            String deskripsi = descriptionField.getText();if (deskripsi == null){deskripsi = "";}

            if (catatanKeuanganTable.countingTotalSpend(kategori,tanggal)+harga > categoryTable.getLimitKategori(kategori) && categoryTable.getLimitKategori(kategori) > 0){
                    if (method.confirmationAlert("Anda Sudah Melebihi Batas Harian "+ kategori+" Klick ok untuk abaikan")){
                        catatanKeuanganTable.addCatatan(kategori,harga,tanggal,deskripsi, method.getNowDateTime()+" limit");
                        method.confirmationAlert("Catatan Berhasil Di Tambahkan");
                        Double uang = Double.parseDouble(priceField.getText().split(" ")[1]);
                        jumlahUangUser.addJumlahUangUser(uang*-1.0);
                        mainPageController.removePopUp();
                    }else{
                        method.confirmationAlert("Catatan Gagal Di Tambahkan");
                    }
            }else{
                catatanKeuanganTable.addCatatan(kategori,harga,tanggal,deskripsi, method.getNowDateTime());
                method.confirmationAlert("Catatan Berhasil Di Tambahkan");
                Double uang = Double.parseDouble(priceField.getText().split(" ")[1]);
                jumlahUangUser.addJumlahUangUser(uang*-1.0);
                mainPageController.removePopUp();
                }
            }
        }
    }

