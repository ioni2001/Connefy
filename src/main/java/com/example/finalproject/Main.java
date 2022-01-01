package com.example.finalproject;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.Message;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.FriendshipValidator;
import com.example.finalproject.domain.validators.UserValidator;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.db.FriendshipDbRepository;
import com.example.finalproject.repository.db.MessageDbRepository;
import com.example.finalproject.repository.db.RequestsDbRepository;
import com.example.finalproject.repository.db.UserDbRepository;
import com.example.finalproject.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 720);
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        Repository<Long, User> userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni", userValidator);
        Repository<Long, Friendship> friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni", friendshipValidator);
        Repository<Long, Message> messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni");
        Repository<Long, Cerere> requestsDbRepository = new RequestsDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni");

        Service userService = new UserService(userDbRepository);
        Service friendshipService = new FriendshipService(friendshipDbRepository);
        Service messageService = new MessageService(messageDbRepository);
        Service requestService = new RequestsService(requestsDbRepository);
        Controller<Long, User, Long, Friendship, Long, Message, Long, Cerere> controller = new Controller((UserService) userService, (FriendshipService) friendshipService, (MessageService) messageService, (RequestsService) requestService);
        LoginController loginController = fxmlLoader.getController();
        loginController.setController(controller);
        stage.setTitle("Connefy");
        stage.setScene(scene);
        loginController.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
