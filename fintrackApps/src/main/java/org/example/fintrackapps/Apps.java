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
//        FXMLLoader fxmlLoader = new FXMLLoader(Apps.class.getResource("MainPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

//        int stageHeight = 576;
//        int stageWidht = 1024;

//        stage.setHeight(stageHeight);
//        stage.setWidth(stageWidht);
//        // Set minimum size
//        stage.setMinHeight(stageHeight);
//        stage.setMinWidth(stageWidht);

        // Set maximum size
//        stage.setMaxHeight(stageHeight);
//        stage.setMaxWidth(stageWidht);

        stage.setTitle("Fintrack");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}