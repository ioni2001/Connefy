package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessengerController implements Observer {

    private Controller service;
    private Stage primaryStage;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TextField messageText;

    @FXML
    private TableView<User> usersTable;

    public void setService(Controller service){
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        usersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        usersTable.setItems(model);
        searchBar.textProperty().addListener(o -> handleFilter());
    }

    private void initModel() {
        List<User> users = service.getFriends(service.findOneByEmail(service.getCurrentEmail()));
        model.setAll(users);
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

    @FXML
    void handleViewConversationBtn(ActionEvent event) throws IOException {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if(selected != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("conversation-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setTitle("Connefy");
            primaryStage.setScene(scene);

            ViewConversationController viewConversationController = fxmlLoader.getController();
            viewConversationController.setUserInConversation(selected);
            viewConversationController.setService(service);
            viewConversationController.setStage(primaryStage);
        }
        else{
            MessageAlert.showErrorMessage(null, "Must select an user!");
        }
    }

    @FXML
    void handleSendBtn(ActionEvent event) {
        List<User> selected = usersTable.getSelectionModel().getSelectedItems();
        String message = messageText.getText();

        if(selected.size() > 0) {
            this.service.sendMessage(selected, message);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Message", "Sent");
        }
        else
        {
            MessageAlert.showErrorMessage(null, "Must select an user!");
        }
    }

    private void handleFilter() {
        Predicate<User> p1 = n -> n.getFirstName().contains(searchBar.getText());
        Predicate<User> p2 = n -> n.getLastName().contains(searchBar.getText());

        List<User> users = (List<User>) service.getFriends(service.findOneByEmail(service.getCurrentEmail()))
                .stream().filter(p1.or(p2)).collect(Collectors.toList());
        model.setAll(users);
    }


    @Override
    public void update(Observable o, Object arg) {
        initModel();
    }
}
