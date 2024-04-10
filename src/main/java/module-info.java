module com.example.tflg_app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.tflg_app to javafx.fxml;
    exports com.example.tflg_app;
}