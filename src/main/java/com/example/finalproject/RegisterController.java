package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    private Controller controller;
    private Stage primaryStage;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public Button getRegister() {
        return register;
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @FXML
    Pane panou;

    @FXML
    TextField email;

    @FXML
    TextField password;

    @FXML
    TextField nume;

    @FXML
    TextField prenume;

    @FXML
    Button register;

    @FXML
    Text inregistrat;

    @FXML
    public void onReg() throws IOException {
        try {
            controller.addUser(nume.getText(), prenume.getText(), email.getText(), password.getText());
            panou.setVisible(false);
            inregistrat.setVisible(true);
            inregistrat.setText("Cont inregistrat");
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Account", "The account was created successfully!");

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setTitle("Connefy");
            primaryStage.setScene(scene);

            LoginController loginController = fxmlLoader.getController();
            loginController.setController(controller);
            loginController.setStage(primaryStage);
        }
        catch (Exception e){
            panou.setVisible(false);
            inregistrat.setVisible(true);
            inregistrat.setText("Cont invalid sau existent !");
            MessageAlert.showErrorMessage(null, "Cont invalid sau existent !");

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("registerNow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setTitle("Connefy");
            primaryStage.setScene(scene);

            RegisterController registerController = fxmlLoader.getController();
            registerController.setController(controller);
            registerController.setStage(primaryStage);
        }

    }

    public void reset(){
        panou.setVisible(true);
        inregistrat.setVisible(false);
        nume.setText("");prenume.setText("");password.setText("");email.setText("");

    }


}
