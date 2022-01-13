package com.example.finalproject;
import com.example.finalproject.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private Controller service;
    private Stage primaryStage;

    @FXML
    private Label userLoggedInLbl;

    @FXML
    private Button req;


    public void setService(Controller service) {
        this.service = service;
        userLoggedInLbl.setText(service.findOneByEmail(service.getCurrentEmail()).getFirstName());
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
    public void handleMyFriendsButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("myfriends-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("My friends");
        primaryStage.setScene(scene);
        MyFriendsController myFriendsController = fxmlLoader.getController();
        myFriendsController.setService(service);
        myFriendsController.setStage(primaryStage);
    }

    @FXML
    public void handleLogOutButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        LoginController loginController = fxmlLoader.getController();
        loginController.setController(service);
        loginController.setStage(primaryStage);
    }

    @FXML
    public void handleRequestsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("requests_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        RequestsController requestsController = fxmlLoader.getController();
        requestsController.setService(service);
        requestsController.setStage(primaryStage);
    }

    @FXML
    public void handleMessangerButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("messanger-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        MessengerController messangerController = fxmlLoader.getController();
        messangerController.setService(service);
        messangerController.setStage(primaryStage);
    }

    @FXML
    void home(){

    }

    public void handleGeneralButton(ActionEvent actionEvent) {
    }

    public void handleMessagesReport(ActionEvent actionEvent) {
    }
}
