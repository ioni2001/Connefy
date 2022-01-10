package com.example.finalproject.repository.db;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;
import com.example.finalproject.utils.Constants;
import com.example.finalproject.utils.HashFunction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;

    public MessageDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Iterable<Message> getAllEntities() {
        Set<Message> messagesList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String fromStr = resultSet.getString("fromtbl");
                String toStr = resultSet.getString("totbl");
                String messageStr = resultSet.getString("messagetbl");
                Long replyID = resultSet.getLong("reply");
                String date = resultSet.getString("datetbl");

                User from = this.getUserFromUsers(fromStr);
                if(from != null) {

                    List<String> toSplit = Arrays.asList(toStr.split(";"));
                    List<User> to = new ArrayList<>();
                    for (String email : toSplit) {
                        if(this.getUserFromUsers(email) != null)
                            to.add(this.getUserFromUsers(email));
                    }

                    Message reply = this.findOne(replyID);
                    Message message = new Message(id, from, to, messageStr, reply, date);
                    messagesList.add(message);
                }
            }
            return messagesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messagesList;
    }

    public User getUserFromUsers(String email){
        String sql = "SELECT * FROM users WHERE email='" + email + "'";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String parola = resultSet.getString(4);
                user = new User(firstName, lastName, email,parola);
                user.setId(resultSet.getLong(1));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(id) FROM messages";
        int size = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            resultSet.next();
            size = resultSet.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Long.valueOf(size);
    }

    @Override
    public Message save(Message entity) {
        if (entity == null)
            throw new EntityNullException();

        String sql = "insert into messages (id, fromtbl ,totbl, messagetbl, reply, datetbl ) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String toStr = "";
            for(User user : entity.getTo()) {
                toStr = toStr.concat(user.getEmail());
                toStr = toStr.concat(";");
            }
            toStr = toStr.substring(0, toStr.length() - 1);
            ps.setInt(1, Integer.parseInt(entity.getId().toString()));
            ps.setString(2, entity.getFrom().getEmail());
            ps.setString(3, toStr);
            ps.setString(4, entity.getMessage());
            if(entity.getReply() == null)
                ps.setInt(5, 0);
            else
                ps.setInt(5, Math.toIntExact(entity.getReply().getId()));
            ps.setString(6, entity.getDate().format(Constants.DATE_TIME_FORMATTER));

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message findOne(Long id) {
        if(id == 0)
            return null;

        String sql = "SELECT * FROM messages WHERE id='" + id + "'";
        Message message = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
                String fromStr = resultSet.getString(2);
                String toStr = resultSet.getString(3);
                String messageStr = resultSet.getString(4);
                String date = resultSet.getString(6);

                User from = this.getUserFromUsers(fromStr);
                List<String> userEmails = Arrays.asList(toStr.split(" "));
                List<User> usersTo = new ArrayList<>();
                for(String email: userEmails)
                    usersTo.add(this.getUserFromUsers(email));

                message = new Message(id,from,usersTo,messageStr,null,date);
                return message;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public String getCurrentEmail() {
        return null;
    }

    @Override
    public List<Long> getAllIDs() {
        return null;
    }

    @Override
    public Message findOneByEmail(String email) {
        return null;
    }

    @Override
    public Message findOneByParola(String parola) {
        return null;
    }

    @Override
    public Iterable<Message> friendshipsOfAnUser(User e) {
        return null;
    }

    @Override
    public void removeFriendship(Long id1, Long id2) {

    }

    @Override
    public void removeFriendRequest(String email1, String email2) {

    }


    @Override
    public Long getCurrentId() {
        return null;
    }

    @Override
    public void setCurrentId(Long currentId) {
    }

}
