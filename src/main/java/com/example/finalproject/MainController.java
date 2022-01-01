package com.example.finalproject;
import com.example.finalproject.Main;
import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import com.example.finalproject.service.Service;
import com.example.finalproject.service.UserService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    private Controller service;
    private Stage primaryStage;

    @FXML
    private Label userLoggedInLbl;


    public void setService(Controller service) {
        this.service = service;
        userLoggedInLbl.setText(service.getCurrentEmail());
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void handleLFSButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("lfs-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Look for someone");
        primaryStage.setScene(scene);

        LookForSomeoneController lookForSomeoneController = fxmlLoader.getController();
        lookForSomeoneController.setService(service);
        lookForSomeoneController.setStage(primaryStage);
    }

    @FXML
    void handleMyFriendsButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("myfriends-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("My friends");
        primaryStage.setScene(scene);

        MyFriendsController myFriendsController = fxmlLoader.getController();
        myFriendsController.setService(service);
        myFriendsController.setStage(primaryStage);
    }

    @FXML
    void handleLogOutButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        LoginController loginController = fxmlLoader.getController();
        loginController.setController(service);
        loginController.setStage(primaryStage);
    }


}
