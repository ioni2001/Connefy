package com.example.finalproject;

import com.example.finalproject.Main;
import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerinte12DTO;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.User;
import com.example.finalproject.service.Service;
import com.example.finalproject.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LookForSomeoneController {

    private Controller service;
    private Stage primaryStage;
    ObservableList<User> model = FXCollections.observableArrayList();



    public void setService(Controller service){
        this.service = service;
        initModel();
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<User> usersTable;

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        usersTable.setItems(model);
        searchBar.textProperty().addListener(o->handleFilter());
    }

    private void initModel() {
        Iterable<User> users = service.getAllUsers();
        List<User> allUsers = new ArrayList<>();
        users.forEach(allUsers::add);
        List<User> usersList = allUsers.stream().filter(x -> !x.getEmail().equals(service.getCurrentEmail())).collect(Collectors.toList());

        model.setAll(usersList);
    }


    @FXML
    void handleAddFriendBtn(ActionEvent event) {
        User selected =usersTable.getSelectionModel().getSelectedItem();

        if(selected != null){
            boolean friendship = false;
            List<User> friendsOfUser = service.getFriends(service.findOneByEmail(service.getCurrentEmail()));
            for(User user:friendsOfUser) {
                if(user.equals(selected)) {
                    friendship = true;
                    MessageAlert.showErrorMessage(null, "Already friends!");
                }
            }
            if(!friendship) {
                service.addCerere(service.getCurrentEmail(), selected.getEmail(), "pending");
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Sent");
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Must select an user!");
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

    private void handleFilter() {
        Predicate<User> p1 = n -> n.getFirstName().contains(searchBar.getText());
        Predicate<User> p2 = n -> n.getLastName().contains(searchBar.getText());

        Iterable<User> users = service.getAllUsers();
        List<User> allUsers = new ArrayList<>();
        users.forEach(allUsers::add);
        List<User> usersList = allUsers.stream().filter(x -> !x.getEmail().equals(service.getCurrentEmail())).collect(Collectors.toList());

        List<User> users2 = usersList
                .stream().filter(p1.or(p2)).collect(Collectors.toList());
        model.setAll(users2);
    }

}