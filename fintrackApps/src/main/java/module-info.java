module org.example.fintrackapps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires quarkus.core;
    requires java.desktop;
    requires itextpdf;
    requires javafx.swing;


    opens org.example.fintrackapps.uiController to javafx.fxml;
    exports org.example.fintrackapps.uiController;

    exports org.example.fintrackapps;
}