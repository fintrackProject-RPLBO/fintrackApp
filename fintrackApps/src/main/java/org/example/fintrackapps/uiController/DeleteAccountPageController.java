package org.example.fintrackapps.uiController;

import javafx.scene.control.TextField;
import org.example.fintrackapps.dataBaseManager.Encryption;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.CatatanKeuanganTable;
import org.example.fintrackapps.tableManager.CategoryTable;
import org.example.fintrackapps.tableManager.JumlahUangUser;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;


public class DeleteAccountPageController {
    UserData userData = UserData.getInstance();
    CategoryTable categoryTable = CategoryTable.getInstance();
    JumlahUangUser jumlahUangUser = JumlahUangUser.getInstance();
    CatatanKeuanganTable catatanKeuanganTable = CatatanKeuanganTable.getInstance();
    Session session = Session.getInstance();
    Encryption encryption = new Encryption(10);
    MethodCollection method = new MethodCollection();

    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordFieldRe_enter;

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField oldPasswordFieldRe_enter;

    @FXML private TextField newUsername;

    @FXML private Button deleteButton;
    @FXML private Button changePasswordButton;
    @FXML private Button changeUsernameButton;

    private MainPageController mainPageController;

    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }

    public DeleteAccountPageController() throws SQLException {
    }

    @FXML
    private void initialize() {
        if (newUsername != null){
            newUsername.setOnAction(event -> changeUsernameButton.fire());
            passwordField.setOnAction(event -> changeUsernameButton.fire());
            passwordFieldRe_enter.setOnAction(event -> changeUsernameButton.fire());
        }
        else if(passwordField != null && passwordFieldRe_enter != null){
            passwordField.setOnAction(event -> deleteButton.fire());
            passwordFieldRe_enter.setOnAction(event -> deleteButton.fire());
        }
        else{
            newPasswordField.setOnAction(event ->changePasswordButton.fire());
            oldPasswordField.setOnAction(event ->changePasswordButton.fire());
            oldPasswordFieldRe_enter.setOnAction(event ->changePasswordButton.fire());
        }
    }

    @FXML
    void deleteBtn() throws SQLException {
        if (session.getUsername() != null){
            String username = session.getUsername();
            String password = passwordField.getText().strip();
            String rePassword = passwordFieldRe_enter.getText().strip();

            String userPassword = encryption.decryption(userData.getUserPassword(username));

            if (password.isEmpty() || rePassword.isEmpty()){
                method.confirmationAlert("password tidak boleh kosong");
            }else{
                if (password.equals(rePassword) && password.equals(userPassword)){
                    if (method.confirmationAlert("Are you sure?")){
                        categoryTable.clearKategori();
                        catatanKeuanganTable.clearCatatan();
                        jumlahUangUser.deleteJumlahUang();
                        userData.deleteAccount(username,password);
                        session.clearSession();
                        mainPageController.showLoginForm();
                    }else{
                        method.confirmationAlert("Delete akun dibatalkan");
                    }
                }else{
                    method.confirmationAlert("Password Salah!");
                }
            }
        }else{
            method.confirmationAlert("anda Belum Login!");
        }
    }

    @FXML
    void changePassword() throws SQLException {
        if (oldPasswordField.getText().equals(oldPasswordFieldRe_enter.getText())){
            userData.changePassword(session.getUsername(),encryption.encryption(oldPasswordField.getText()),encryption.encryption(newPasswordField.getText()));
            mainPageController.removePopUp();
        }
    }

    @FXML
    void changeUsername() throws SQLException {
        if (passwordField.getText().equals(passwordFieldRe_enter.getText())){
            userData.changeUsername(newUsername.getText(), encryption.encryption(passwordField.getText()));
            mainPageController.removePopUp();
        }
    }



    @FXML
    void cancelBtn() throws SQLException {
        if (newUsername != null){
            newUsername.clear();
            passwordField.clear();
            passwordFieldRe_enter.clear();
        }
        else if(passwordField != null && passwordFieldRe_enter != null){
            passwordField.clear();
            passwordFieldRe_enter.clear();
        }
        else{
            newPasswordField.clear();
            oldPasswordField.clear();
            oldPasswordFieldRe_enter.clear();
        }
        mainPageController.removePopUp();
//        formSetController.removeForm();
    }

}
