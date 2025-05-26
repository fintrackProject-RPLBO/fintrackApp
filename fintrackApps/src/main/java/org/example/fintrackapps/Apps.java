package org.example.fintrackapps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Apps extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Apps.class.getResource("Container.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);

        stage.setTitle("Fintrack");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}