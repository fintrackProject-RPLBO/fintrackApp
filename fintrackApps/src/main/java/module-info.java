module org.example.fintrackapps {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fintrackapps to javafx.fxml;
    exports org.example.fintrackapps;
}