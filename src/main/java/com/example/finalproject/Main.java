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
import com.example.finalproject.repository.file.FriendshipFileRepository;
import com.example.finalproject.repository.file.UserFileRepository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;
import com.example.finalproject.repository.memory.UserMemoryRepository;
import com.example.finalproject.service.*;
import com.example.finalproject.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

/*        UserFileRepository userFileRepository = new UserFileRepository("src/userFile.cvs",userValidator);
        FriendshipFileRepository friendshipFileRepository = new FriendshipFileRepository("src/friendshipFile.cvs",friendshipValidator);*/

        Repository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni", userValidator);
        Repository friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni", friendshipValidator);
        Repository messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni");
        Repository requestsDbRepository = new RequestsDbRepository("jdbc:postgresql://localhost:2001/SocialNetwork", "postgres", "ioni");

        Service userService = new UserService(userDbRepository);
        Service friendshipService = new FriendshipService(friendshipDbRepository);
        Service messageService = new MessageService(messageDbRepository);
        Service requestService = new RequestsService(requestsDbRepository);

        Controller<Long, User, Long, Friendship, Long, Message, Long, Cerere> controller = new Controller((UserService) userService, (FriendshipService) friendshipService, (MessageService) messageService, (RequestsService) requestService);

        UserInterface<Long,User, Long, Friendship, Long, Message, Long, Cerere> Ui = new UserInterface<>(controller);
        Ui.startUI();
    }
}
