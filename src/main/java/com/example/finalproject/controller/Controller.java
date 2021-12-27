package com.example.finalproject.controller;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.UserValidator;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.LogInException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.service.FriendshipService;
import com.example.finalproject.service.MessageService;
import com.example.finalproject.service.RequestsService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.utils.Constants;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Controller <ID, E extends Entity<ID>, ID2, E2 extends Entity<ID2>, ID3, E3 extends Entity<ID3>, ID4, E4 extends Entity<ID4>>{
    private UserService<ID, E> userService;
    private FriendshipService<ID2, E2> friendshipService;
    private MessageService<ID3, E3> messageService;
    private RequestsService<ID4,E4> requestsService;

    public Controller(UserService<ID, E> userService, FriendshipService<ID2, E2> friendshipService, MessageService<ID3, E3> messageService, RequestsService<ID4,E4> requestsService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.requestsService = requestsService;
    }

    public int numberOfCommunities(){
        return friendshipService.numberOfCommunities(userService.getAllIDs());
    }

    public List<User> mostSociableCommunity(){
        List<Long> rez = friendshipService.mostSociableCommunity(userService.getAllIDs());
        List<User> users = new ArrayList<>();
        for(Long i : rez)
            users.add(userService.getUser(i));
        return users;
    }

    public Iterable<E> getAllUsers(){
        return userService.getAll();
    }

    public Iterable<E2> getAllFriendships(){
        return friendshipService.getAll();
    }

    public void logIn(String email){
        ID id;
        boolean ok = false;
        Iterable<User> entities = (Iterable<User>) userService.getAll();
        for(User user: entities){
            if(user.getEmail().equals(email)) {
                this.userService.setCurrentUserId((ID) user.getId());
                ok = true;
                break;
            }
        }
        if(!ok)
            throw new NotExistanceException();
    }

    public String getCurrentEmail(){
        return userService.getCurrentEmail();
    }

    public void addUser(String firstName, String lastName, String email){
        User user = new User(firstName, lastName, email);
        this.userService.add((E)user);
    }

    public void removeUser(String email){
        User userToDel = userService.getUserByEmail(email);
        friendshipService.removeAll((E2)userToDel);
        userService.remove((E)userToDel);
    }

    public void addFriend(String email){
        User userToAdd = userService.getUserByEmail(email);
        ID id = userService.getCurrentId();
        if( (Long) id == -1L)
            throw new LogInException();
        friendshipService.setCurrentUserId((ID2)id);
        friendshipService.add((E2)userToAdd);
    }

    public void removeFriend(String email){
        User userToDel = userService.getUserByEmail(email);
        ID id = userService.getCurrentId();
        if( (Long) id == -1L)
            throw new LogInException();
        friendshipService.setCurrentUserId((ID2)id);
        friendshipService.remove((E2)userToDel);
    }

    public List<User> getFriends(User user){
        List<User> friends = new ArrayList<>();
        Iterable<E2> friendships = friendshipService.getAll();
        for(E2 friendship:friendships){
            if(((Friendship)friendship).getTuple().getLeft().equals(user.getId()))
                friends.add(userService.getUser(((Friendship)friendship).getTuple().getRight()));
            else
                if(((Friendship)friendship).getTuple().getRight().equals(user.getId()))
                    friends.add(userService.getUser(((Friendship)friendship).getTuple().getLeft()));
        }
        return friends;
    }

    public void updateUser(String email, String firstName, String lastName){
        User user = userService.getUserByEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userService.update((E)user);
    }

    public void updateFriendship(String email1, String email2){
        User user1 = userService.getUserByEmail(email1);
        User user2 = userService.getUserByEmail(email2);
        Friendship friendship = new Friendship(new Tuple<>(user1.getId(), user2.getId()), "");
        friendshipService.update((E2)friendship);
    }

    public User findOneByEmail(String email){
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            throw new ValidationException("Email invalid!");
        return userService.findOneByEmail(email);
    }

    public List<Cerinte12DTO> lab5_1_srv(User a){
        Iterable<Friendship> friendships = (Iterable<Friendship>) friendshipService.getAll();
        List<Friendship> friendships1 = new ArrayList<Friendship>();
        for(Friendship f: friendships){
            friendships1.add(f);
        }
        List<Cerinte12DTO> cerinte12DTOS = friendships1.stream()
                    .filter(x -> x.getTuple().getLeft().equals(a.getId()))
                    .map(x -> new Cerinte12DTO(userService.getUser(x.getTuple().getRight()).getLastName(),
                            userService.getUser(x.getTuple().getRight()).getFirstName(),
                            x.getDate())).collect(Collectors.toList());
        cerinte12DTOS.addAll(friendships1.stream()
                .filter(x -> x.getTuple().getRight().equals(a.getId()))
                .map(x -> new Cerinte12DTO(userService.getUser(x.getTuple().getLeft()).getLastName(),
                        userService.getUser(x.getTuple().getLeft()).getFirstName(),
                        x.getDate())).collect(Collectors.toList()));
        return cerinte12DTOS;
    }

    public List<Cerinte12DTO> friendshipsByMonth(User user, String month){
        if(Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12)
            throw new ValidationException("Invalid month!");

        Iterable<Friendship> friendships = (Iterable<Friendship>) friendshipService.getAll();
        List<Friendship> list = new ArrayList<>();
        friendships.forEach(list::add);
        List<Cerinte12DTO> result = list.stream()
                .filter(x -> x.getTuple().getLeft().equals(user.getId()) && x.getDate().substring(5, 7).equals(month))
                .map(x -> new Cerinte12DTO(userService.getUser(x.getTuple().getRight()).getLastName(),
                        userService.getUser(x.getTuple().getRight()).getFirstName(), x.getDate())).
                collect(Collectors.toList());
        result.addAll(list.stream()
                .filter(x -> x.getTuple().getRight().equals(user.getId()) && x.getDate().substring(5, 7).equals(month))
                .map(x -> new Cerinte12DTO(userService.getUser(x.getTuple().getLeft()).getLastName(),
                        userService.getUser(x.getTuple().getLeft()).getFirstName(), x.getDate())).
                collect(Collectors.toList()));
        return result;
    }

    public void sendMessage(List<User> to, String messageStr) {
        ID id = userService.getCurrentId();
        if( (Long) id == -1L)
            throw new LogInException();

        Iterable<Friendship> friendships = (Iterable<Friendship>) friendshipService.getAll();
        for(User user : to) {
            boolean ok = false;
            for (Friendship friendship : friendships)
                if (friendship.getTuple().getRight().equals(user.getId()) && friendship.getTuple().getLeft().equals(userService.getCurrentId()) ||
                        friendship.getTuple().getLeft().equals(user.getId()) && friendship.getTuple().getRight().equals(userService.getCurrentId())){
                    ok = true;
                    break;
                }
            if(!ok)
                throw new ValidationException("Not a friend!");
        }

        Message message = new Message(this.messageService.size() + 1, userService.getUser((Long) userService.getCurrentId()), to, messageStr, null, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        this.messageService.add((E3) message);
    }

    public List<Message> userMessages() {
        List<Message> listAux = new ArrayList<>();
        User user = userService.getUser((Long) userService.getCurrentId());
        Iterable<Message> listOfMessages = (Iterable<Message>) this.messageService.getAll();
        for(Message message : listOfMessages)
            if(message.getTo().contains(user))
                listAux.add(message);

        return listAux;
    }

    public void replyMessage(Long id, String messageStr) {
        if(id > this.messageService.size() + 1)
            throw new NotExistanceException();

        Iterable<Message> listOfMessages = (Iterable<Message>) this.messageService.getAll();
        Message reply = null;
        for(Message message : listOfMessages){
            if(message.getId().equals(id)){
                reply = new Message(this.messageService.size() + 1, userService.getUser((Long) userService.getCurrentId()), Arrays.asList(message.getFrom()), messageStr, message, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
                break;
            }
        }
        this.messageService.add((E3) reply);
    }

    public List<Message> viewConversation(String firstEmail, String secondEmail) {
        List<Message> listAux = new ArrayList<>();
        User firstUser = userService.getUserByEmail(firstEmail);
        User secondUser = userService.getUserByEmail(secondEmail);

        Iterable<Friendship> friendships = (Iterable<Friendship>) friendshipService.getAll();
        boolean ok = false;
        for(Friendship friendship : friendships)
            if (friendship.getTuple().getLeft().equals(firstUser.getId()) && friendship.getTuple().getRight().equals(secondUser.getId()) || friendship.getTuple().getRight().equals(firstUser.getId()) && friendship.getTuple().getLeft().equals(secondUser.getId())){
                ok = true;
                break;
        }
        if(!ok)
            throw new ValidationException("Users are not friends!");

        Iterable<Message> listOfMessages = (Iterable<Message>) this.messageService.getAll();
        for(Message message:listOfMessages) {
            if(message.getFrom().equals(firstUser) && message.getTo().contains(secondUser) ||
               message.getFrom().equals(secondUser) && message.getTo().contains(firstUser))
                listAux.add(message);
        }
        return listAux.stream().sorted(Comparator
                        .comparing(Message::getDate))
                        .collect(Collectors.toList());
    }

    public void addCerere(String email1, String email2, String status){
        Cerere c = new Cerere(email1, email2,status);
        this.requestsService.add((E4) c);
    }

    public Cerere getReqById(Long id){
        for(Object c: this.getRequests(getCurrentEmail())){
            if(((Cerere) c).getId() == id){
                return (Cerere) c;
            }
        }
        return null;
    }

    public void removeCerere(Long id){
        if(getReqById(id) != null)
            requestsService.remove((E4) getReqById(id));
    }

    public Iterable<Cerere> getRequests(String email){
        return this.requestsService.getRequestsByEmail(email);
    }

    public void updateRequest(Cerere c){
        requestsService.update((E4) c);
    }
}
