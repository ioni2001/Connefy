package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.User;
//import com.sun.javafx.collections.ElementObservableListDecorator;
import com.example.finalproject.domain.validators.FriendshipValidator;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
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

    @FXML
    private Button accept;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<Cerere> cereri;

    @FXML
    public void initialize() {
        status.setCellValueFactory(new PropertyValueFactory<Cerere, String>("status"));
        date.setCellValueFactory(new PropertyValueFactory<Cerere, String>("date"));
        from.setCellValueFactory(new PropertyValueFactory<Cerere, String>("email_sender"));
        cereri.setItems(model);
        searchBar.textProperty().addListener(o->handleFilter());
    }


    public void acceptButt(){
        Cerere a = this.cereri.getSelectionModel().getSelectedItem();
        try{
            if(a.getStatus().equals("pending")) {
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

    private void initModel() {
        Iterable<Cerere> cerereIterable = service.getRequests(service.getCurrentEmail());
        List<Cerere> cereri = new ArrayList<>();
        cerereIterable.forEach(cereri::add);
        List<Cerere> cerereList = cereri.stream().filter(x -> x.getEmail_recv().equals(service.getCurrentEmail())).collect(Collectors.toList());

        model.setAll(cerereList);
    }

    private void handleFilter() {
        Predicate<Cerere> p1 = n -> n.getEmail_sender().contains(searchBar.getText());
        Predicate<Cerere> p2 = n -> n.getStatus().contains(searchBar.getText());

        Iterable<Cerere> cereri = service.getRequests(service.getCurrentEmail());
        List<Cerere> allCereri = new ArrayList<>();
        cereri.forEach(allCereri::add);
        List<Cerere> cerereList = allCereri.stream().filter(x -> x.getEmail_recv().equals(service.getCurrentEmail())).collect(Collectors.toList());

        List<Cerere> cereri2 = cerereList
                .stream().filter(p1.or(p2)).collect(Collectors.toList());
        model.setAll(cereri2);
    }

    public void declineButt(ActionEvent actionEvent) {
        Cerere a = this.cereri.getSelectionModel().getSelectedItem();
        if(a.getStatus().equals("pending")) {
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
        if(a.getStatus().equals("pending")) {
            service.removeRequest(a.getId());
            initModel();
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Canceled !");
        }
        else{
            MessageAlert.showMessage( null, Alert.AlertType.INFORMATION,null,"Already declined or approved !");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        initModel();
    }
}
