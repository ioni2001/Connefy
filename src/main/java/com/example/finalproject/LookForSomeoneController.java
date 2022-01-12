package com.example.finalproject;

import com.example.finalproject.Main;
import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerinte12DTO;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.PageableImpl;
import com.example.finalproject.service.Service;
import com.example.finalproject.service.UserService;
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
import java.util.stream.StreamSupport;

public class LookForSomeoneController implements Observer {

    private Controller service;
    private Stage primaryStage;
    ObservableList<User> model = FXCollections.observableArrayList();

    public void setService(Controller service){
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

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
    private TableView<User> usersTable;

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        usersTable.setItems(model);
        searchBar.textProperty().addListener(o->handleFilter());
        addUsersTableScrollbarListener();
    }

    private void initModel() {
        initUsers();
    }

    private void addUsersTableScrollbarListener() {
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) usersTable.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 0.0) {
                    if (firstLoadedUsersPage.getPageable().getPageNumb() > 1) {
                        secondLoadedUsersPage = firstLoadedUsersPage;
                        firstLoadedUsersPage = service.getAllUsers(firstLoadedUsersPage.previousPageable());
                        setUserModel();
                    }
                } else if ((Double) newValue == 1.0) {
                    if (secondLoadedUsersPage.getContent().size() == secondLoadedUsersPage.getPageable().getPageSize()) {
                        Page<User> newUsers = service.getAllUsers(secondLoadedUsersPage.nextPageable());

                        if (!newUsers.getContent().isEmpty()) {
                            firstLoadedUsersPage = secondLoadedUsersPage;
                            secondLoadedUsersPage = newUsers;
                            setUserModel();
                        }
                    }
                }
            });
        });
    }

    private void initUsers(){
        firstLoadedUsersPage = service.getAllUsers(new PageableImpl<>(1, 4));
        secondLoadedUsersPage = service.getAllUsers(new PageableImpl<>(2, 4));
        setUserModel();
    }

    private void setUserModel(){
        List<User> users = firstLoadedUsersPage.getContent();
        users.addAll(secondLoadedUsersPage.getContent());
        users.removeIf(user -> user.getEmail().equals(service.getCurrentEmail()));
        model.setAll(users);
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
                boolean ok = false;
                try {
                    service.addCerere(service.getCurrentEmail(), selected.getEmail(), "pending");
                    ok = true;
                }
                catch(Exception e){
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
                if(ok)
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

    @Override
    public void update(Observable o, Object arg) {
        initModel();
    }
}