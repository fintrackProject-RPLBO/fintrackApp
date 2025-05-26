package org.example.fintrackapps.uiController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.fintrackapps.dataBaseManager.Session;
import org.example.fintrackapps.tableManager.UserData;

import java.io.IOException;
import java.sql.SQLException;

public class ContainerController {
    UserData userdata = UserData.getInstance();
    Session session = Session.getInstance();
    MethodCollection method = new MethodCollection();

    private Node formNode; // To keep track of the form.fxml content
    private String nodePath = "";
    @FXML private VBox VBoxContainer;

    public ContainerController() throws SQLException {
    }

    @FXML
    void initialize() throws SQLException {
        if (session.getUsername() == null){
            ShowLoginForm();
        }else{
            ShowMainPage();
        }
    }

    @FXML
    public void ShowMainPage() throws SQLException {
        removeForm();
        String path = "/org/example/fintrackapps/MainPage.fxml";
        pushFxml(path);
    }

    @FXML
    public void ShowLoginForm() throws SQLException {
        removeForm();
        String path = "/org/example/fintrackapps/LoginPage.fxml";
        pushFxml(path);
    }

    @FXML
    public void ShowRegisterForm() throws SQLException {
        removeForm();
        String path = "/org/example/fintrackapps/RegisterPage.fxml";
        pushFxml(path);
    }

    public void pushFxml(String path) {
        try {
            removeForm();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent fxmlRoot = loader.load();

            Object controller = loader.getController();
            if (controller instanceof LoginPageController loginController) {
                loginController.setContainerController(this);
            } else if (controller instanceof MainPageController mainController) {
                mainController.setContainerController(this);
            } else if (controller instanceof RegisterPageControl registerController) {
                registerController.setContainerController(this);
            }

            VBoxContainer.getChildren().setAll(fxmlRoot);
            formNode = fxmlRoot;
            nodePath = path;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeForm() {
        if (formNode != null) {
//            mainVBox.getChildren().remove(formNode);
            VBoxContainer.getChildren().clear();
            VBoxContainer.setStyle("-fx-background-color: transparent;");
            formNode = null;
            nodePath = null;
        }
    }
}
