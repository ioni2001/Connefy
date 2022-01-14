package com.example.finalproject;

import com.example.finalproject.Main;
import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.User;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.PageableImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MyFriendsController implements Observer {
    private Controller service;
    private Stage primaryStage;
    ObservableList<User> model = FXCollections.observableArrayList();
    private Page<User> firstLoadedUsersPage;
    private Page<User> secondLoadedUsersPage;

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableView<User> tableView;

    @FXML
    private Label userLoggedInLbl;

    public void setService(Controller service){
        this.service = service;
        this.service.addObserver(this);
        userLoggedInLbl.setText(service.findOneByEmail(service.getCurrentEmail()).getFirstName() + " " + service.findOneByEmail(service.getCurrentEmail()).getLastName());
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
        tableView.setItems(model);
        searchBar.textProperty().addListener(o -> handleFilter());
        addUsersTableScrollbarListener();
    }

    private void initModel() {
        initFriends();
    }

    private void addUsersTableScrollbarListener() {
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
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
    void handleRemoveFriendBtn(ActionEvent event) {
        User selected = tableView.getSelectionModel().getSelectedItem();

        if(selected != null){
            service.removeFriend(selected);
            service.removeRequestBetweeen(selected.getEmail(), service.getCurrentEmail());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete friend", "Deleted");
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

        List<User> users = (List<User>) service.getFriends(service.findOneByEmail(service.getCurrentEmail()))
                        .stream().filter(p1.or(p2)).collect(Collectors.toList());
        model.setAll(users);
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

    public void home() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        MainController mainController = fxmlLoader.getController();
        mainController.setService(service);
        mainController.setStage(primaryStage);
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
    public void chatImg() throws IOException {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("conversation-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            primaryStage.setTitle("Connefy");
            primaryStage.setScene(scene);

            ViewConversationController viewConversationController = fxmlLoader.getController();
            viewConversationController.setUserInConversation(selected);
            viewConversationController.setService(service);
            viewConversationController.setStage(primaryStage);
        } else {
            MessageAlert.showErrorMessage(null, "Must select an user!");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        initModel();
    }
}
