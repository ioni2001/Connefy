package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class RegisterController {

    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public Button getRegister() {
        return register;
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
    public void onReg(){
        try {
            controller.addUser(nume.getText(), prenume.getText(), email.getText(), password.getText());
            panou.setVisible(false);
            inregistrat.setVisible(true);
            inregistrat.setText("Cont inregistrat");
        }
        catch (Exception e){
            panou.setVisible(false);
            inregistrat.setVisible(true);
            inregistrat.setText("Cont invalid sau existent !");
        }

    }

    public void reset(){
        panou.setVisible(true);
        inregistrat.setVisible(false);
        nume.setText("");prenume.setText("");password.setText("");email.setText("");

    }


}
