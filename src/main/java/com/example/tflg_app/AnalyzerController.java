package com.example.tflg_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AnalyzerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}