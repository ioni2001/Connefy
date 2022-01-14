package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.User;
//import com.sun.javafx.collections.ElementObservableListDecorator;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RequestsController implements Observer {

    private Controller service;
    private Stage primaryStage;
    ObservableList<Cerere> model = FXCollections.observableArrayList();


    public void setService(Controller service) {
        this.service = service;
        this.service.addObserver(this);
        userLoggedInLbl.setText(service.findOneByEmail(service.getCurrentEmail()).getFirstName() + " " + service.findOneByEmail(service.getCurrentEmail()).getLastName());
        initModel();
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private TableColumn<Cerere, String> from;

    @FXML
    private TableColumn<Cerere, String> status;

    @FXML
    private TableColumn<Cerere, String> date;

    private Page<Cerere> firstLoadedCererePage;
    private Page<Cerere> secondLoadedCererePage;

    @FXML
    private Button accept;

    @FXML
    private Label userLoggedInLbl;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<Cerere> cereri;

    @FXML
    private RadioButton sent;

    @FXML
    private RadioButton recv;

    @FXML
    public void initialize() {
        status.setCellValueFactory(new PropertyValueFactory<Cerere, String>("status"));
        date.setCellValueFactory(new PropertyValueFactory<Cerere, String>("date"));
        from.setCellValueFactory(new PropertyValueFactory<Cerere, String>("email_sender"));
        cereri.setItems(model);
        searchBar.textProperty().addListener(o->handleFilter());
        addReqsTableScrollbarListener();
        recv.setSelected(true);
    }


    public void acceptButt(){
        Cerere a = this.cereri.getSelectionModel().getSelectedItem();
        try{
            if(a.getStatus().equals("pending") && a.getEmail_recv().equals(service.getCurrentEmail())) {
                service.addFriend(a.getEmail_sender());
                a.setStatus("approved");
                service.updateRequest(a);
            }
            else{
                MessageAlert.showErrorMessage(null, "Already friends !");
            }
        }
        catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        catch(ExistanceException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    public void handleBackBtn(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Connefy");
        primaryStage.setScene(scene);

        MainController mainController = fxmlLoader.getController();
        mainController.setService(service);
        mainController.setStage(primaryStage);
    }

    private void addReqsTableScrollbarListener() {
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) cereri.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 0.0) {
                    if (firstLoadedCererePage.getPageable().getPageNumb() > 1) {
                        secondLoadedCererePage = firstLoadedCererePage;
                        firstLoadedCererePage = service.getRequests(firstLoadedCererePage.previousPageable(),service.getCurrentEmail());
                        setReqsModel();
                    }
                } else if ((Double) newValue == 1.0) {
                    if (secondLoadedCererePage.getContent().size() == secondLoadedCererePage.getPageable().getPageSize()) {
                        Page<Cerere> newReqs = service.getRequests(secondLoadedCererePage.nextPageable(), service.getCurrentEmail());

                        if (!newReqs.getContent().isEmpty()) {
                            firstLoadedCererePage = secondLoadedCererePage;
                            secondLoadedCererePage = newReqs;
                            setReqsModel();
                        }
                    }
                }
            });
        });
    }

    private void addReqsSentTableScrollbarListener() {
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) cereri.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 0.0) {
                    if (firstLoadedCererePage.getPageable().getPageNumb() > 1) {
                        secondLoadedCererePage = firstLoadedCererePage;
                        firstLoadedCererePage = service.getRequests(firstLoadedCererePage.previousPageable(),service.getCurrentEmail());
                        setReqsSentModel();
                    }
                } else if ((Double) newValue == 1.0) {
                    if (secondLoadedCererePage.getContent().size() == secondLoadedCererePage.getPageable().getPageSize()) {
                        Page<Cerere> newReqs = service.getRequests(secondLoadedCererePage.nextPageable(), service.getCurrentEmail());

                        if (!newReqs.getContent().isEmpty()) {
                            firstLoadedCererePage = secondLoadedCererePage;
                            secondLoadedCererePage = newReqs;
                            setReqsSentModel();
                        }
                    }
                }
            });
        });
    }

    private void initReqs(){
        firstLoadedCererePage = service.getRequests(new PageableImpl<>(1, 4),service.getCurrentEmail());
        secondLoadedCererePage = service.getRequests(new PageableImpl<>(2, 4),service.getCurrentEmail());
        setReqsModel();
    }

    private void setReqsModel(){
        List<Cerere> cereri = firstLoadedCererePage.getContent();
        cereri.addAll(secondLoadedCererePage.getContent());
        model.setAll(cereri);
    }

    private void initSentReqs(){
        firstLoadedCererePage = service.getSentReqs(new PageableImpl<>(1, 4),service.getCurrentEmail());
        secondLoadedCererePage = service.getSentReqs(new PageableImpl<>(2, 4),service.getCurrentEmail());
        setReqsSentModel();
    }

    private void setReqsSentModel(){
        List<Cerere> cereri = firstLoadedCererePage.getContent();
        cereri.addAll(secondLoadedCererePage.getContent());
        model.setAll(cereri);
    }

    private void initModel() {
        initReqs();
    }

    private void handleFilter() {
        Predicate<Cerere> p1 = n -> n.getEmail_sender().contains(searchBar.getText());
        Predicate<Cerere> p2 = n -> n.getStatus().contains(searchBar.getText());

        if(sent.isDisable()) {
            Iterable<Cerere> cereri = service.getRequests(service.getCurrentEmail());
            List<Cerere> allCereri = new ArrayList<>();
            cereri.forEach(allCereri::add);
            List<Cerere> cerereList = allCereri.stream().filter(x -> x.getEmail_recv().equals(service.getCurrentEmail())).collect(Collectors.toList());

            List<Cerere> cereri2 = cerereList
                    .stream().filter(p1.or(p2)).collect(Collectors.toList());
            model.setAll(cereri2);
        }
        else{
            List<Cerere> cereri = (List<Cerere>) service.getAllSent(service.getCurrentEmail());
            List<Cerere> cereri2 = cereri
                    .stream().filter(p1.or(p2)).collect(Collectors.toList());
            model.setAll(cereri2);
        }
    }

    public void declineButt(ActionEvent actionEvent) {
        Cerere a = this.cereri.getSelectionModel().getSelectedItem();
        if(a.getStatus().equals("pending") && a.getEmail_recv().equals(service.getCurrentEmail())) {
            a.setStatus("declined");
            service.updateRequest(a);
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Declined !");
        }
        else{
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Already declined or approved !");
        }
    }

    public void cancelButt(ActionEvent actionEvent) {
        Cerere a = this.cereri.getSelectionModel().getSelectedItem();
        System.out.println(a);
        Long id = a.getId();
        if(a.getStatus().equals("pending") && a.getEmail_sender().equals(service.getCurrentEmail())) {
            service.removeRequest(id);
            initModel();
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Canceled !");
        }
        else{
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Already declined or approved !");
        }
    }

    public void onSent(){
        addReqsSentTableScrollbarListener();
        initSentReqs();
        status.setCellValueFactory(new PropertyValueFactory<Cerere, String>("status"));
        date.setCellValueFactory(new PropertyValueFactory<Cerere, String>("date"));
        from.setCellValueFactory(new PropertyValueFactory<Cerere, String>("email_recv"));
        from.setText("To");
        recv.setSelected(false);
    }

    public void onRecv(){
        initModel();
        from.setCellValueFactory(new PropertyValueFactory<Cerere, String>("email_sender"));
        from.setText("From");
        sent.setSelected(false);
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
    void handleMyFriendsButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("myfriends-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("My friends");
        primaryStage.setScene(scene);
        MyFriendsController myFriendsController = fxmlLoader.getController();
        myFriendsController.setService(service);
        myFriendsController.setStage(primaryStage);
    }
    @Override
    public void update(Observable o, Object arg) {
        if(sent.isDisable())
            initModel();
        else
            initSentReqs();
    }
}
