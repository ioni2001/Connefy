package com.example.finalproject.ui;

import com.example.finalproject.controller.Controller;
import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.*;
//import jdk.swing.interop.SwingInterOpUtils;
import com.example.finalproject.service.UserService;
import com.example.finalproject.service.UserService;
import com.example.finalproject.service.FriendshipService;
import com.example.finalproject.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface <ID, E extends Entity<ID>, ID2, E2 extends Entity<ID2>, ID3, E3 extends Entity<ID3>, ID4, E4 extends Entity<ID4>> implements UI<ID, E, ID2, E2, ID3, E3,ID4, E4> {

    private Controller controller;
    private Scanner in;

    public UserInterface(Controller<ID, E, ID2, E2, ID3, E3, ID4, E4> controller) {
        this.controller = controller;
        this.in = new Scanner(System.in);
    }

    private void printOptions(){
        System.out.println("-------------MENU------------");
        System.out.println("Email logged in: " + controller.getCurrentEmail());
        System.out.println("Choose an option");
        System.out.println("1.  Print all");
        System.out.println("2.  Log In");
        System.out.println("3.  Add user");
        System.out.println("4.  Remove user");
        System.out.println("5.  Add friend");
        System.out.println("6.  Remove friend");
        System.out.println("7.  Number of communities");
        System.out.println("8.  The most sociable community");
        System.out.println("9.  Update an user");
        System.out.println("10. Update a friendship");
        System.out.println("11. Find one");
        System.out.println("12. Laborator5_Cerinta1");
        System.out.println("13. Friendships by month");
        System.out.println("14. Send a message");
        System.out.println("15. Reply message");
        System.out.println("16. View conversation");
        System.out.println("17. Cereri de prietenie");
        System.out.println("18. Reply all");
        System.out.println("19.  Exit");
        System.out.println("-------------MENU------------");

    }


    @Override
    public void startUI() {
            boolean run = true;
            while (run) {
                try{
                this.printOptions();
                String input = in.next();
                switch (input) {
                    case "19":
                        run = false;
                        break;
                    case "1":
                        this.printAll();
                        break;
                    case "2":
                        this.logIn();
                        break;
                    case "3":
                        this.addUser();
                        break;
                    case "4":
                        this.removeUser();
                        break;
                    case "5":
                        this.addFriend();
                        break;
                    case "6":
                        this.removeFriend();
                        break;
                    case "7":
                        this.numberOfCommunities();
                        break;
                    case "8":
                        this.mostSociableCommunity();
                        break;
                    case "9":
                        this.updateUser();
                        break;
                    case "10":
                        this.updateFriendship();
                        break;
                    case "11":
                        this.findOneByEmail();
                        break;
                    case "12":
                        this.lab5_1();
                        break;
                    case "13":
                        this.friendshipsByMonth();
                        break;
                    case "14":
                        this.sendMessage();
                        break;
                    case "15":
                        this.replyMessage();
                        break;
                    case "16":
                        this.viewConversation();
                        break;
                    case "17":
                        this.lab5_4();
                        break;
                    case "18":
                        this.replyAll();
                        break;
                    default:
                        System.out.println("Invalid option!");
                }
        }catch(ValidationException | EntityNullException | IdNullException | ExistanceException | LogInException | NotExistanceException ex) {
                    System.out.println(ex.getMessage());
                }
            }
    }

    @Override
    public void findOneByEmail() {
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println(controller.findOneByEmail(email).toString());
    }

    @Override
    public void updateUser(){
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println("Enter the new first name: ");
        String firstName = in.next();
        System.out.println("Enter the new last name: ");
        String lastName = in.next();
        controller.updateUser(email, firstName, lastName);
    }

    @Override
    public void updateFriendship() {
        System.out.println("Enter the email of the first user: ");
        String email1 = in.next();
        System.out.println("Enter the email of the second user: ");
        String email2 = in.next();
        controller.updateFriendship(email1, email2);
    }

    @Override
    public void numberOfCommunities(){
        System.out.println("The number of communities is: " + controller.numberOfCommunities());
    }

    @Override
    public void mostSociableCommunity(){
        System.out.println("The most sociable community is: ");
        List<User> rez = controller.mostSociableCommunity();
        for(User user:rez)
            System.out.println(user.toString());
    }

    @Override
    public void printAll() {
        Iterable<User> users = (Iterable<User>) controller.getAllUsers();
        for(User user: users){
            System.out.println(user.toString());
            System.out.println("Friends: ");
            List<User> friends = controller.getFriends(user);
            for(User friend : friends) {
                System.out.println(friend.toString());
            }
            System.out.println();
            friends.clear();
        }
        Iterable<Friendship> friendships = (Iterable<Friendship>) controller.getAllFriendships();
        for(Friendship friendship:friendships){
            System.out.println(friendship.toString());
            System.out.println();
        }
    }

    @Override
    public void logIn() {
        this.printAll();
        System.out.println("Enter email to log in: ");
        String email = in.next();
        System.out.println("Enter password: ");
        String password = in.next();
        controller.logIn(email,password);
    }

    @Override
    public void addUser() {
        System.out.println("Enter first name: ");
        String firstName = in.next();
        System.out.println("Enter last name: ");
        String lastName = in.next();
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println("Password: ");
        String parola = in.next();
        controller.addUser(firstName, lastName, email,parola);
    }

    @Override
    public void removeUser() {
        System.out.println("Enter email: ");
        String email = in.next();
        controller.removeUser(email);
    }

    @Override
    public void addFriend() {
        System.out.println("Enter email: ");
        String email = in.next();
        controller.addFriend(email);
    }

    @Override
    public void removeFriend() {
        System.out.println("Enter email: ");
        String email = in.next();
    }

    @Override
    public void lab5_1() {
        System.out.println("Mail: ");
        String email = in.next();
        User u = controller.findOneByEmail(email);
        List<Cerinte12DTO> cerinte12DTOS = controller.lab5_1_srv(u);
        for(Cerinte12DTO a : cerinte12DTOS){
            System.out.println(a.toString());
        }
    }

    @Override
    public void friendshipsByMonth() {
        System.out.println("Enter email: ");
        String email = in.next();
        System.out.println("Enter month: ");
        String month = in.next();
        User user = controller.findOneByEmail(email);
        List<Cerinte12DTO> result = controller.friendshipsByMonth(user, month);
        for(Cerinte12DTO obj : result){
            System.out.println(obj.toString());
        }
    }

    @Override
    public void sendMessage() {
        List<User> to = new ArrayList<>();
        String answer = "yes";
        while(!answer.equals("no")) {
            System.out.println("Enter friend's email to send a message: ");
            String email = in.next();

            User user = controller.findOneByEmail(email);
            to.add(user);

            System.out.println("Wanna send the message to another friend?  yes/no");
            answer = in.next();
        }
        System.out.println("Message: ");
        in.nextLine();
        String message = in.nextLine();
        controller.sendMessage(to, message);

    }

    @Override
    public void replyMessage() {
        List<Message> listAux = controller.userMessages();
        if(listAux.size() == 0)
            throw new ValidationException("You have no messages!");

        for(Message message:listAux){
            System.out.println(message.getId()
            + "|"
            + message.getDate().format(Constants.DATE_TIME_FORMATTER)
            + "|"
            + message.getFrom().getFirstName() + " " + message.getFrom().getLastName()
            + "|"
            + message.getMessage());
        }
        System.out.println("Which message do you wanna reply? ");
        Long id = Long.parseLong(in.next());
        System.out.println("Message: ");
        in.nextLine();
        String message = in.nextLine();
        controller.replyMessage(id, message);
    }

    @Override
    public void replyAll() {
        List<Message> listAux = controller.getMessagesFromGroup();
        if(listAux.size() == 0)
            throw new ValidationException("You have no messages from groups!");

        for(Message message:listAux){
            System.out.println(message.getId()
                    + "|"
                    + message.getDate().format(Constants.DATE_TIME_FORMATTER)
                    + "|"
                    + message.getFrom().getFirstName() + " " + message.getFrom().getLastName()
                    + "|"
                    + message.getMessage());
        }

        System.out.println("Which message do you wanna reply? ");
        Long id = Long.parseLong(in.next());
        System.out.println("Message: ");
        in.nextLine();
        String message = in.nextLine();
        controller.replyAll(id, message);
    }

    @Override
    public void viewConversation() {
        System.out.println("Enter the emails of the two:");
        String firstEmail = in.next();
        String secondEmail = in.next();
        List<Message> messages = controller.viewConversation(firstEmail, secondEmail);
        for(Message message: messages) {
            System.out.println(message.getDate().format(Constants.DATE_TIME_FORMATTER)
            + "|"
            + message.getFrom().getFirstName() + " " + message.getFrom().getLastName()
            + "|"
            + message.getMessage());
            if(message.getReply() != null) {
                System.out.println("|"
                        + "-----> REPLIED: "
                        + message.getReply().getDate().format(Constants.DATE_TIME_FORMATTER)
                        + "|"
                        + message.getReply().getFrom().getFirstName() + " " + message.getReply().getFrom().getLastName()
                        + "|"
                        + message.getReply().getMessage());
            }
        }
    }

    @Override
    public void lab5_4() {
        System.out.println("Options: ");
        System.out.println("1. Print requests of the logged user");
        System.out.println("2. Send a request");
        System.out.println("3. Accept/Decline a request");
        String inp = in.next();
        if(inp.equals("1")){
            for(Object c: controller.getRequests(controller.getCurrentEmail())){
                System.out.println(c.toString());
            }
        }
        else if(inp.equals("2")){
            System.out.println("Introduceti un email: ");
            String email = in.next();
            controller.addCerere(controller.getCurrentEmail(), email, "pending");
        }
        else if(inp.equals("3")){
            System.out.println("Introduceti un email: ");
            String email = in.next();
            System.out.println("1.Accept");
            System.out.println("2.Decline");
            System.out.println(email);
            String inp11 = in.next();
            Long id = -1L;
            for(Object c: controller.getRequests(controller.getCurrentEmail())){
                if(((Cerere) c).getEmail_sender().equals(email)){
                    id = ((Cerere) c).getId();
                }
            }
            if(inp11.equals("1") && id!=-1L){
                Cerere c = controller.getReqById(id);
                c.setStatus("approved");
                controller.updateRequest(c);
                controller.addFriend(email);
            }
            else if(inp11.equals("2") && id!=-1L){
                System.out.println("Ceau");
                Cerere c = controller.getReqById(id);
                c.setStatus("rejected");
                controller.updateRequest(c);
            }
        }
        System.out.println("Operation ended");
    }
}