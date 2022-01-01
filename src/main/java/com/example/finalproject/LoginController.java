package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.utils.HashFunction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Reflection;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private Controller controller;
    private User activeUser;
    private Stage primaryStage;

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Controller getController() {
        return controller;
    }

    public Button getLogButt() {
        return logButt;
    }

    public Button getRegister() {
        return register;
    }

    @FXML
    TextField username;

    @FXML
    TextField password;

    @FXML
    Text validate;

    @FXML
    Button logButt;

    @FXML
    Button register;


    @FXML
    protected void onLogin() throws IOException {
        if(validation()){
            controller.logIn(username.textProperty().getValue(),password.textProperty().getValue());
            logButt.setEffect(new Bloom());

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setTitle("Connefy");
            primaryStage.setScene(scene);

            MainController mainController = fxmlLoader.getController();
            mainController.setService(controller);
            mainController.setStage(primaryStage);
        }
    }

    @FXML
    protected void onRegister() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("registerNow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        RegisterController registerController = fxmlLoader.getController();
        registerController.setController(controller);
        registerController.setStage(primaryStage);
    }

    @FXML
    protected void onHover(){
        logButt.setOpacity(0.7);
    }

    @FXML
    protected void outHover(){
        logButt.setOpacity(1);
    }

    @FXML
    public void initialize(){
        username.textProperty().addListener(observable -> validation());
        password.textProperty().addListener(observable -> validation());
    }

    private boolean validation(){
        HashFunction hashFunction = new HashFunction();
        try {
            if (controller.findOneByEmail(username.textProperty().getValue()) != null && controller.findOneByParola(hashFunction.getHash(password.textProperty().getValue(),"MD5")) != null) {
                if( controller.findOneByEmail(username.textProperty().getValue()).equals(controller.findOneByParola(hashFunction.getHash(password.textProperty().getValue(),"MD5")))) {
                    validate.setText("Cont valid !");
                    Reflection r = new Reflection();
                    r.setFraction(0.7f);
                    validate.setEffect(r);
                    return true;
                }
            }
        }
        catch(NotExistanceException e){
            validate.setText("");
        }
        catch(ValidationException e){
            validate.setText("");
        }
        return false;
    }
}
