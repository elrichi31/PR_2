module proyecto2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyecto2 to javafx.fxml;
    exports proyecto2;
}
