package com.example.tflg_app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AnalyzerController implements Initializable {
    @FXML
    private TextField stringField;
    @FXML
    private TextField idField;
    @FXML
    private TextField constField;
    @FXML
    private TextArea errorField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stringField.textProperty().addListener((observable, oldValue, newValue) -> {
            Result result = CheckLoopOperator.Check(newValue);
            idField.setText(result.getListOfIDs().toString());
            constField.setText(result.getListOfConst().toString());

            if (result.getErrType() != Err.NoError){
                errorField.setText("Ошибка: " + result.getErr() + " на позиции "
                        + result.getErrPosition() + " после " + newValue.substring(0, result.getErrPosition()));
                idField.setText("");
                constField.setText("");
            }
            else {
                errorField.setText("");
                idField.setText("IDs: " + result.getListOfIDs());
                constField.setText("Константы: " + result.getListOfConst());

            }
        });
    }
}