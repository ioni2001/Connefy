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
import com.example.finalproject.utils.HashFunction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginView.fxml"));
        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene1 = new Scene(fxmlLoader1.load(), 1000, 720);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 720);
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

/*        UserFileRepository userFileRepository = new UserFileRepository("src/userFile.cvs",userValidator);
        FriendshipFileRepository friendshipFileRepository = new FriendshipFileRepository("src/friendshipFile.cvs",friendshipValidator);*/

        Repository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnet", "postgres", "230516", userValidator);
        Repository friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnet", "postgres", "230516", friendshipValidator);
        Repository messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnet", "postgres", "230516");
        Repository requestsDbRepository = new RequestsDbRepository("jdbc:postgresql://localhost:5432/socialnet", "postgres", "230516");

        Service userService = new UserService(userDbRepository);
        Service friendshipService = new FriendshipService(friendshipDbRepository);
        Service messageService = new MessageService(messageDbRepository);
        Service requestService = new RequestsService(requestsDbRepository);
        Controller<Long, User, Long, Friendship, Long, Message, Long, Cerere> controller = new Controller((UserService) userService, (FriendshipService) friendshipService, (MessageService) messageService, (RequestsService) requestService);
        LoginController loginController = fxmlLoader.getController();
        loginController.setController(controller);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Stage stage1 = new Stage();
        stage1.setScene(scene1); //aici bagi scena ta cu mesaje, sau meniu ce ai
        loginController.getLogButt().setOnAction(e -> {
            try {
                loginController.onLogin();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            stage1.show(); //aici ii dai show
            System.out.println(loginController.getController().getCurrentEmail());
            HashFunction function = new HashFunction();
            String parola = function.getHash("cont1","MD5");
            System.out.println(parola);
        });
        }

    public static void main(String[] args) {
        launch();
    }
}