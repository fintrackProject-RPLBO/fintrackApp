package org.example.fintrackapps.uiController;

import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.UserData;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;

public class LoginPageController {
    UserData userdata = UserData.getInstance();
    Session session = Session.getInstance();
    MethodCollection method = new MethodCollection();

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Button loginBtn;
    private ContainerController containerController;

    public void setContainerController(ContainerController controller) {
        this.containerController = controller;
    }

    public LoginPageController() throws SQLException {

    }

    @FXML
    private void initialize() {
        tfUsername.setOnAction(event -> loginBtn.fire());
        pfPassword.setOnAction(event -> loginBtn.fire());
    }

    @FXML
    protected void loginBtn() throws SQLException {
        String username = tfUsername.getText().strip();
        String password = pfPassword.getText().strip();
        if(username.isEmpty() || password.isEmpty()){
            method.confirmationAlert("username dan password tidak boleh kosong");
        }
        else if(userdata.login(username,password) == 0){
            method.confirmationAlert("login berhasil!");
            session.setUsername(username);
            containerController.ShowMainPage();
        }
        else if(userdata.login(username,password) == 1){
            method.confirmationAlert("password salah!");
        }
        else{
            method.confirmationAlert("akun tidak ditemukan!");
        }


    }


    @FXML
    protected void registerBtn() throws SQLException {
        containerController.removeForm();
        containerController.ShowRegisterForm();

    }


    public void setKeyAction(Scene scene){
        scene.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER){

            }
        });
    }

}