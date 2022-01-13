package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class StatisticsController {

    private Controller service;
    private Stage primaryStage;

    public void setService(Controller service){
        this.service = service;
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private DatePicker endDate;

    @FXML
    private TextField friendEmail;

    @FXML
    private DatePicker startDate;

    @FXML
    void handleGeneralButton(ActionEvent event) {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        if(start.isAfter(end)){
            MessageAlert.showErrorMessage(null, "Invalid time interval!");
        }
        else{
            try{
                this.service.saveGeneralReport(this.service.getCurrentEmail(), Timestamp.valueOf(start.atStartOfDay().plusDays(1).minusSeconds(1)), Timestamp.valueOf(end.atStartOfDay()));
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Report generated successfully into E:/PDFExport/reports.pdf !");
            }catch (IOException e) {
                MessageAlert.showErrorMessage(null, "Could not save report as pdf!");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleMessagesReport(ActionEvent event) {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        if(start.isAfter(end)){
            MessageAlert.showErrorMessage(null, "Invalid time interval!");
        }
        else{
            String friend = friendEmail.getText();
            try{
                if (friendEmail.getText().isEmpty()) {
                    MessageAlert.showErrorMessage(null, "Please write your friend email first!");
                    return;
                }
                this.service.saveConversation(this.service.getCurrentEmail(), friend, Timestamp.valueOf(start.atStartOfDay().plusDays(1).minusSeconds(1)), Timestamp.valueOf(end.atStartOfDay()));
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Report generated successfully!");
            }catch (IOException e) {
                MessageAlert.showErrorMessage(null, "Could not save report as pdf!");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleBackBtn(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        MainController mainController = fxmlLoader.getController();
        mainController.setService(service);
        mainController.setStage(primaryStage);
    }
}
