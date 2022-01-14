package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.User;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.PageableImpl;
import javafx.application.Platform;
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
    private Page<User> firstLoadedUsersPage;
    private Page<User> secondLoadedUsersPage;

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
        addUsersTableScrollbarListener();
    }

    private void initModel() {
        initFriends();
    }

    private void addUsersTableScrollbarListener() {
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) usersTable.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 0.0) {
                    if (firstLoadedUsersPage.getPageable().getPageNumb() > 1) {
                        secondLoadedUsersPage = firstLoadedUsersPage;
                        firstLoadedUsersPage = service.friendsOfAnUser(firstLoadedUsersPage.previousPageable(), service.findOneByEmail(service.getCurrentEmail()));
                        setFriendModel();
                    }
                } else if ((Double) newValue == 1.0) {
                    if (secondLoadedUsersPage.getContent().size() == secondLoadedUsersPage.getPageable().getPageSize()) {
                        Page<User> newUsers = service.friendsOfAnUser(secondLoadedUsersPage.nextPageable(), service.findOneByEmail(service.getCurrentEmail()));

                        if (!newUsers.getContent().isEmpty()) {
                            firstLoadedUsersPage = secondLoadedUsersPage;
                            secondLoadedUsersPage = newUsers;
                            setFriendModel();
                        }
                    }
                }
            });
        });
    }

    private void initFriends(){
        firstLoadedUsersPage = service.friendsOfAnUser(new PageableImpl<>(1, 4), service.findOneByEmail(service.getCurrentEmail()));
        secondLoadedUsersPage = service.friendsOfAnUser(new PageableImpl<>(2, 4), service.findOneByEmail(service.getCurrentEmail()));
        setFriendModel();
    }

    private void setFriendModel(){
        List<User> friends = firstLoadedUsersPage.getContent();
        friends.addAll(secondLoadedUsersPage.getContent());
        model.setAll(friends);
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
