package com.example.finalproject.repository.db;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.UserMemoryRepository;
import com.example.finalproject.utils.HashFunction;

import java.sql.*;
import java.util.*;

public class UserDbRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    private HashFunction hashFunction = new HashFunction();
    private Object currentId;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.currentId = -1L;
    }

    @Override
    public Long getCurrentId() {
        return (Long) currentId;
    }

    @Override
    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }

    @Override
    public User findOne(Long aLong) {
        Iterable<User> users = this.getAllEntities();
        for(User user:users)
            if(user.getId().equals(aLong))
                return user;
        throw new NotExistanceException();
    }

    @Override
    public Iterable<User> getAllEntities() {
        Set<User> usersList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                String email = resultSet.getString("email");
                String parola = resultSet.getString("parola");

                User user = new User(firstName, lastName, email,parola);
                user.setId(id);
                usersList.add(user);
            }
            return usersList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(email) FROM users";
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
    public User save(User entity) {
        if (entity == null)
            throw new EntityNullException();

        validator.validate(entity);

        Iterable<User> users = this.getAllEntities();
        for(User user:users){
            if(entity.getEmail().equals(user.getEmail()))
                throw new ExistanceException();
        }
        String sql = "insert into users (id, firstname, lastname, email, parola , data ) values (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt((entity.getId().toString())));
            ps.setString(2, (entity).getFirstName());
            ps.setString(3, (entity).getLastName());
            ps.setString(4, (entity).getEmail());
            String parola = hashFunction.getHash((entity).getParola(),"MD5");
            ps.setString(5, parola);

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long aLong) {
        Iterable<User> users = this.getAllEntities();
        User userToDel = null;
        for(User user : users){
            if(user.getId().equals(aLong)) {
                userToDel = user;
                break;
            }
        }
        int idToDel = Integer.parseInt(aLong.toString());
        String sql = "delete from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idToDel);
            ps.executeUpdate();
            return userToDel;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userToDel;
    }

    @Override
    public User update(User entity) {
        String sql = "update users set firstname = ?, lastname = ? where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ((User)entity).getFirstName());
            ps.setString(2, ((User)entity).getLastName());
            ps.setInt(3, Integer.parseInt(entity.getId().toString()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public User findOneByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email='" + email + "'";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            String parola = resultSet.getString(5);
            user = new User(firstName, lastName, email, parola);
            user.setId(resultSet.getLong(1));
            return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User findOneByParola(String parola) {
        String sql = "SELECT * FROM users WHERE parola='" + parola + "'";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();) {
            if(resultSet.next()){
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                user = new User(firstName, lastName, email, parola);
                user.setId(resultSet.getLong(1));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public String getCurrentEmail() {
        if(this.getCurrentId() == -1)
            return null;
        return this.findOne(this.getCurrentId()).getEmail();
    }

    @Override
    public List<Long> getAllIDs() {
        List<Long> rez = new ArrayList<>();
        Iterable<User>users = this.getAllEntities();
        for(User user:users)
            rez.add(user.getId());
        return rez;
    }
}
