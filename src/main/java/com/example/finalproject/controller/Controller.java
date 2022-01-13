package com.example.finalproject.controller;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.UserValidator;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.LogInException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.service.FriendshipService;
import com.example.finalproject.service.MessageService;
import com.example.finalproject.service.RequestsService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.utils.Constants;
import com.example.finalproject.utils.HashFunction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.sql.Timestamp;
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

    public void logIn(String email, String parola){
        ID id;
        boolean ok = false;
        HashFunction hashFunction = new HashFunction();
        String parola_hash = hashFunction.getHash(parola,"MD5");
        User user = this.findOneByEmail(email);
        if(user.getParola().equals(parola_hash)){
            this.userService.setCurrentUserId((ID) user.getId());
            ok = true;
        }
        if(!ok)
            throw new NotExistanceException();
    }

    public String getCurrentEmail(){
        return userService.getCurrentEmail();
    }

    public void addUser(String firstName, String lastName, String email, String parola){
        User user = new User(firstName, lastName, email, parola);
        this.userService.add((E)user);
    }

    public void removeUser(String email){
        User userToDel = userService.findOneByEmail(email);
        friendshipService.removeAll((E2)userToDel);
        userService.remove((E)userToDel);
    }

    public void addFriend(String email){
        User userToAdd = userService.findOneByEmail(email);
        ID id = userService.getCurrentId();
        if( (Long) id == -1L)
            throw new LogInException();
        friendshipService.setCurrentUserId((ID2)id);
        friendshipService.add((E2)userToAdd);
    }

    public void removeFriend(User userToDel){
        ID id = userService.getCurrentId();
        if( (Long) id == -1L)
            throw new LogInException();
        friendshipService.setCurrentUserId((ID2)id);
        friendshipService.remove((E2)userToDel);
    }

    public List<User> getFriends(User user){
        List<User> friends = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipService.getFriends(user);
        for(Friendship friendship:friendships) {
            if (!friendship.getTuple().getLeft().equals(user.getId()))
                friends.add(this.userService.getUser(friendship.getTuple().getLeft()));
            else
                friends.add(this.userService.getUser(friendship.getTuple().getRight()));
        }
        return friends;
    }

    public void updateUser(String email, String firstName, String lastName){
        User user = userService.findOneByEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userService.update((E)user);
    }

    public void updateFriendship(String email1, String email2){
        User user1 = userService.findOneByEmail(email1);
        User user2 = userService.findOneByEmail(email2);
        Friendship friendship = new Friendship(new Tuple<>(user1.getId(), user2.getId()), "");
        friendshipService.update((E2)friendship);
    }

    public User findOneByEmail(String email){
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            throw new ValidationException("Email invalid!");
        return userService.findOneByEmail(email);
    }

    public User findOneByParola(String parola){
        return userService.findOneByParola(parola);
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
        Message message = new Message(this.messageService.size() + 1, userService.getUser((Long) userService.getCurrentId()), to, messageStr, null, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        this.messageService.add((E3) message);
    }

    public boolean isIn(List<User> list, User user) {
        for(User user1:list)
            if(user1.getEmail().equals(user.getEmail()))
                return true;
        return false;
    }

    public List<Message> userMessages() {
        List<Message> listAux = new ArrayList<>();
        User user = userService.getUser((Long) userService.getCurrentId());
        Iterable<Message> listOfMessages = (Iterable<Message>) this.messageService.getAll();
        for(Message message : listOfMessages)
            if (isIn(message.getTo(), user))
                listAux.add(message);
        return listAux.stream().sorted(Comparator
                        .comparing(Message::getDate))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesFromGroup() {
        List<Message> listAux = new ArrayList<>();
        User user = userService.getUser((Long) userService.getCurrentId());
        Iterable<Message> listOfMessages = (Iterable<Message>) this.messageService.getAll();
        for(Message message : listOfMessages){
            if(message.getTo().size() > 1 && isIn(message.getTo(), user))
                listAux.add(message);
        }
        return listAux;
    }

    public void replyMessage(Long id, String messageStr) {
        Message message = this.messageService.findOne((ID3) id);
        Message reply = new Message(this.messageService.size() + 1, userService.getUser((Long) userService.getCurrentId()), Arrays.asList(message.getFrom()), messageStr, message, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        this.messageService.add((E3) reply);
    }

    public void replyAll(Long id, String messageStr) {
        Message message = this.messageService.findOne((ID3) id);
        List<User> to = message.getTo();
        to.add(message.getFrom());
        to.remove(this.userService.findOneByEmail(this.getCurrentEmail()));
        Message reply = new Message(this.messageService.size() + 1, userService.getUser((Long) userService.getCurrentId()), to, messageStr, message, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        this.messageService.add((E3) reply);
    }

    public List<Message> viewConversation(String firstEmail, String secondEmail) {
        List<Message> listAux = this.messageService.conversation(firstEmail, secondEmail);
        return listAux.stream().sorted(Comparator
                        .comparing(Message::getDate))
                        .collect(Collectors.toList());
    }

    public void addCerere(String email1, String email2, String status){
        Cerere c = new Cerere(email1, email2,status,LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        this.requestsService.add((E4) c);
    }

    public Cerere getReqById(Long id){
        for(Object c: this.getRequests(getCurrentEmail())){
            if(((Cerere) c).getId().equals(id)){
                return (Cerere) c;
            }
        }
        for(Object c: this.getAllSent(getCurrentEmail())){
            if(((Cerere) c).getId().equals(id)){
                return (Cerere) c;
            }
        }
        return null;
    }

    public void removeRequestBetweeen(String email1, String email2){
        requestsService.removeCerere(email1, email2);
    }

    public void removeRequest(Long id){
        requestsService.remove((E4) getReqById(id));
    }

    public Iterable<Cerere> getRequests(String email){
        return this.requestsService.getRequestsByEmail(email);
    }

    public void updateRequest(Cerere c){
        requestsService.update((E4) c);
    }

    public void addObserver(Observer obj) {
        friendshipService.addObserver(obj);
        messageService.addObserver(obj);
        requestsService.addObserver(obj);
        userService.addObserver(obj);
    }

    public Page<User> getAllUsers(Pageable<User> pageable){
        return this.userService.getAllEntities(pageable);
    }
    public Page<Cerere> getRequests(Pageable<Cerere> pageable,String email){
        return this.requestsService.getReqByName(pageable,email);
    }

    public void saveConversation(String loggedUserEmail, String friendEmail, Timestamp start, Timestamp end) throws IOException {
        List<Message> messagesAux = this.viewConversation(loggedUserEmail, friendEmail);
        List<Message> messages = new ArrayList<>();

        int size = 0;
        for(Message message:messagesAux) {
            if(!message.getFrom().getEmail().equals(loggedUserEmail) && message.getDate().isAfter(start.toLocalDateTime()) && message.getDate().isBefore(end.toLocalDateTime())){
                messages.add(message);
                size++;
            }
        }

        StringBuilder body = new StringBuilder();
        if(size == 0){
            body.append("You have no messages received from your friend in the time period selected!");
        }
        else {
            body.append("You have ").append(size).append(" messages received from your friend in the time period selected!").append("\n\n");

            User friend = this.findOneByEmail(friendEmail);
            for (Message message : messages) {
                body.append("New message from: ").append(friend.getFirstName()).append(" ").append(friend.getLastName()).append("\nMessage: \n\"").append(message.getMessage())
                        .append("\"\n").append("Time: ").append(message.getDate().format(Constants.DATE_TIME_FORMATTER)).append("\n\n");
            }
        }
            saveTextToPdf(body.toString());
    }

    public void saveGeneralReport(String loggedUserEmail, Timestamp start, Timestamp end) throws IOException{
        List<Message> messagesAux = this.userMessages();
        List<Message> messages = new ArrayList<>();

        int size = 0;
        for(Message message:messagesAux) {
            if(!message.getFrom().getEmail().equals(loggedUserEmail) && message.getDate().isAfter(start.toLocalDateTime()) && message.getDate().isBefore(end.toLocalDateTime())){
                messages.add(message);
                size++;
            }
        }
        StringBuilder body = new StringBuilder();
        if(size == 0){
            body.append("You have no messages received in the time period selected!");
        }
        else {
            body.append("You have ").append(size).append(" messages received in the time period selected!").append("\n\n");

            for (Message message : messages) {
                body.append("New message from: ").append(message.getFrom().getFirstName()).append(" ").append(message.getFrom().getLastName()).append("\nMessage: \n\"").append(message.getMessage())
                        .append("\"\n").append("Time: ").append(message.getDate().format(Constants.DATE_TIME_FORMATTER)).append("\n\n");
            }
        }

        size = 0;
        Iterable<Friendship> friendshipsAux = this.friendshipService.getFriends(this.findOneByEmail(this.getCurrentEmail()));
        List<Friendship> friendships = new ArrayList<>();
        for(Friendship friendship:friendshipsAux){
            if(LocalDateTime.parse(friendship.getDate(), Constants.DATE_TIME_FORMATTER).isAfter(start.toLocalDateTime()) && LocalDateTime.parse(friendship.getDate(), Constants.DATE_TIME_FORMATTER).isBefore(end.toLocalDateTime())){
                size++;
                friendships.add(friendship);
            }
        }
        if(size == 0){
            body.append("You have no new friendships made in the time period selected!");
        }
        else {
            body.append("You have ").append(size).append(" new friendships made in the time period selected!").append("\n\n");

            for(Friendship friendship:friendships){
                if(friendship.getTuple().getLeft().equals(this.userService.getCurrentId()))
                    body.append("You became friend with ").append(this.userService.getUser(friendship.getTuple().getRight()).getFirstName()).append(" ").append(this.userService.getUser(friendship.getTuple().getRight()).getLastName()).append(" on ").append(friendship.getDate()).append("\n");
                else
                    body.append("You became friend with ").append(this.userService.getUser(friendship.getTuple().getLeft()).getFirstName()).append(" ").append(this.userService.getUser(friendship.getTuple().getLeft()).getLastName()).append(" on ").append(friendship.getDate()).append("\n");
            }
        }
        saveTextToPdf(body.toString());
    }

    public Iterable<Cerere> getAllSent(String email){
        return this.requestsService.getSentReqs(email);
    }

    private void saveTextToPdf(String text) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
        contentStream.beginText();
        contentStream.newLineAtOffset(25, 700);
        contentStream.setLeading(14.5f);
        List<String> chunks = List.of(text.split("\n"));

        for (int index = 0, availableRows = 25; index < chunks.size(); ++index, --availableRows) {
            if (availableRows == 0) {
                availableRows = 25;
                contentStream.endText();
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(25, 700);
                contentStream.setLeading(14.5f);
            }

            contentStream.showText(chunks.get(index));
            contentStream.newLine();
            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        document.save("E:/PDFExport/reports.pdf");
        document.close();
    }
}
