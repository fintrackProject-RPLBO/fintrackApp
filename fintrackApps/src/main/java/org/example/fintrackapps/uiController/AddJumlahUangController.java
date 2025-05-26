package org.example.fintrackapps.uiController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.JumlahUangUser;
import org.example.fintrackapps.tableManager.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddJumlahUangController {
    UserData userData = UserData.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    Session session = Session.getInstance();
    MethodCollection method = new MethodCollection();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();

    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }

    @FXML private TextField priceField;
    @FXML private TextField descriptionField;
    @FXML private Button btn;


    public AddJumlahUangController() throws SQLException {
    }

    @FXML
    private void initialize() throws SQLException {
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
    void addingJumlahUangUser(ActionEvent event) throws SQLException {
        Button clicked = (Button) event.getSource();
        Double uang = Double.parseDouble(priceField.getText().split(" ")[1]);

        if (clicked.getText().equals("Tambah")){
            jumlahUangUser.addJumlahUangUser(uang);
            catatanKeuanganTable.addCatatan("+",uang,method.getNowDate(),descriptionField.getText(),method.getNowDateTime()+" surplus");
        }else if (clicked.getText().equals("Edit")){
            jumlahUangUser.editJumlahUangUser(uang);
        }

        mainPageController.removePopUp();
    }

}
