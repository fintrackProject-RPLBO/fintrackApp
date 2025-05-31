package org.example.fintrackapps.uiController;

import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.JumlahUangUser;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EditCatatanPageController {
    UserData userData = UserData.getInstance();
    Session session = Session.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    MethodCollection method = new MethodCollection();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();

    @FXML
    private ComboBox<String> category;
    @FXML private TextField priceField;
    @FXML private DatePicker date;
    @FXML private TextField descriptionField;
    String lastUpdate;

    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }

    public EditCatatanPageController() throws SQLException {}


    @FXML
    private void initialize() throws SQLException {
        ArrayList<Object[]> data = categoryTable.getAllDataKategori();
        for (Object[] i : data){
            category.getItems().add(i[0].toString());
        }

        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.startsWith("Rp. ")) {
                priceField.setText("Rp. " + newVal.replace("Rp. ", ""));
            }});

        setupValue();
    }

    @FXML
    private void setupValue() throws SQLException {
        String kategori = session.getClickedDataCatatan()[0].toString();
        Double harga = Double.parseDouble( session.getClickedDataCatatan()[1].toString());
        String tanggal =  session.getClickedDataCatatan()[2].toString();
        String deskripsi =  session.getClickedDataCatatan()[3].toString();
        lastUpdate =  session.getClickedDataCatatan()[5].toString();
        System.out.println(lastUpdate);


        category.setValue(kategori);
        priceField.setText(String.valueOf(harga));
        date.setValue(LocalDate.parse(tanggal));
        descriptionField.setText(deskripsi);
    }

    @FXML
    private void editCatatan() throws SQLException {
        String originalCategory = session.getClickedDataCatatan()[0].toString();
        Double totalSpendingBefore = catatanKeuanganTable.countingTotalSpend(category.getValue(),date.getValue().toString());
        Double limitCategory = categoryTable.getLimitKategori(category.getValue());
        Double originalPrice = Double.parseDouble( session.getClickedDataCatatan()[1].toString());
        Double editedPrice = Double.parseDouble(priceField.getText().split(" ")[1]);

        if (originalCategory.equals(category.getValue())){
            if (totalSpendingBefore-originalPrice+editedPrice < limitCategory){
                if (lastUpdate.contains("surplus")){
                    System.out.println("surplus");
                    jumlahUangUser.addJumlahUangUser(originalPrice*-1.0);
                    jumlahUangUser.addJumlahUangUser(editedPrice);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime()+" surplus");
                }else{
                    jumlahUangUser.addJumlahUangUser(originalPrice);
                    jumlahUangUser.addJumlahUangUser(editedPrice*-1.0);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime());
                }
                mainPageController.removePopUp();
                session.unsetClickedDataKategori();
                System.out.println("same category");
            }else{
                if (lastUpdate.contains("surplus")){
                    System.out.println("surplus");
                    jumlahUangUser.addJumlahUangUser(originalPrice*-1.0);
                    jumlahUangUser.addJumlahUangUser(editedPrice);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime()+" surplus");
                }else{
                    jumlahUangUser.addJumlahUangUser(originalPrice);
                    jumlahUangUser.addJumlahUangUser(editedPrice*-1.0);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime());
                }
                mainPageController.removePopUp();
                session.unsetClickedDataKategori();
                System.out.println("same category limit");
            }
        }else{
            if (totalSpendingBefore+editedPrice < limitCategory){
                if (lastUpdate.contains("surplus")){
                    System.out.println("surplus");
                    jumlahUangUser.addJumlahUangUser(originalPrice*-1.0);
                    jumlahUangUser.addJumlahUangUser(editedPrice);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime()+" surplus");
                }else{
                    jumlahUangUser.addJumlahUangUser(originalPrice);
                    jumlahUangUser.addJumlahUangUser(editedPrice*-1.0);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime());
                }
                mainPageController.removePopUp();
                session.unsetClickedDataKategori();
                System.out.println("diff category");
            }else{
                if (lastUpdate.contains("surplus")){
                    System.out.println("surplus");
                    jumlahUangUser.addJumlahUangUser(originalPrice*-1.0);
                    jumlahUangUser.addJumlahUangUser(editedPrice);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime()+" surplus");
                }else{
                    jumlahUangUser.addJumlahUangUser(originalPrice);
                    jumlahUangUser.addJumlahUangUser(editedPrice*-1.0);
                    catatanKeuanganTable.editCatatan(category.getValue(), editedPrice, date.getValue().toString(),descriptionField.getText(), method.getNowDateTime());
                }
                mainPageController.removePopUp();
                session.unsetClickedDataKategori();
                System.out.println("diff category limit");
            }
        }
    }

    @FXML
    private void deleteCatatan() throws SQLException {
        catatanKeuanganTable.deleteCatatan();
        mainPageController.removePopUp();
        session.unsetClickedDataKategori();
    }

}
