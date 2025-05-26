package org.example.fintrackapps.uiController;

import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class RegisterPageControl {
    UserData userdata = UserData.getInstance();
    MethodCollection method = new MethodCollection();
    CategoryTable categoryTable = CategoryTable.getInstance();

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordFieldRe_Enter;

    private ContainerController containerController;

    public void setContainerController(ContainerController controller) {
        this.containerController = controller;
    }
    public RegisterPageControl() throws SQLException {
    }

    @FXML
    protected void loginBtn() throws SQLException {
        containerController.removeForm();
        containerController.ShowLoginForm();
    }

    @FXML
    protected void registerBtn() throws SQLException {
        String username = tfUsername.getText().strip();
        String password = passwordField.getText().strip();
        String passwordRe_enter = passwordFieldRe_Enter.getText().strip();

        if (password.equals(passwordRe_enter)){
            if (userdata.isExist(username)){
                method.confirmationAlert("Username sudah terdaftar");
            }
            else{
                if (username.length() < 5){
                    method.confirmationAlert( "username minimal 5 karakter");
                }else if (password.length() < 8){
                    method.confirmationAlert("password too short! atleast use 8 character minimum");
                }else if(!method.isValid(password)){
                    method.confirmationAlert("pasword harus memiliki minimal 6 huruf, 1 angka, dan 1 simbol!");
                }else{
                    if(username.isEmpty() || password.isEmpty()){
                        method.confirmationAlert("username dan password tidak boleh kosong");
                    }
                    else if(userdata.register(username,password)){
                        categoryTable.addKategori(50000.0,"Makanan",username, "Harian");
                        categoryTable.addKategori(50000.0,"Transportasi",username, "Harian");
                        categoryTable.addKategori(50000.0,"Pakaian",username, "Harian");
                        categoryTable.addKategori(50000.0,"Lainnya",username, "Harian");
                        method.confirmationAlert("akun berhasil di daftarkan!");
                        containerController.ShowLoginForm();
                    }else {
                        method.confirmationAlert("register gagal");
                    }
                }
            }
        }else{
            method.confirmationAlert("Password Re-enter tidak sama");
        }
    }





}
