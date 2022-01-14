package com.example.finalproject.repository.db;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.PageImpl;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Long, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    private Object currentId;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.currentId = -1L;
    }

    @Override
    public Long getCurrentId() {
        return (Long) this.currentId;
    }

    @Override
    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }

    @Override
    public Friendship findOne(Long aLong) {
        Iterable<Friendship> friendships = this.getAllEntities();
        for(Friendship friendship:friendships){
            if(friendship.getId().equals(aLong))
                return friendship;
        }
        throw new NotExistanceException();
    }

    @Override
    public Iterable<Friendship> getAllEntities() {
        Set<Friendship> friendshipsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long left = resultSet.getLong("leftv");
                Long right = resultSet.getLong("rightv");
                String date = resultSet.getString("datef");

                Friendship friendship = new Friendship(new Tuple<>(left, right), date);
                friendship.setId(id);
                friendshipsList.add(friendship);
            }
            return friendshipsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsList;
    }

    @Override
    public Long getEntitiesCount() {
        String sql = "SELECT COUNT(id) FROM friendships";
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
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new EntityNullException();

        validator.validate(entity);

        String sql = "insert into friendships (id, leftv, rightv, datef ) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(entity.getId().toString()));
            ps.setInt(2, Math.toIntExact(entity.getTuple().getLeft()));
            ps.setInt(3, Math.toIntExact(entity.getTuple().getRight()));
            ps.setString(4, entity.getDate());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Long aLong) {
        int idToDel = Integer.parseInt(aLong.toString());
        String sql = "delete from friendships where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idToDel);
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        String sql = "update friendships set datef = ? where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ((Friendship)entity).getDate());
            ps.setInt(2, Integer.parseInt(entity.getId().toString()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public User getUserFromUsers(Long id){
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String parola = resultSet.getString(5);
                user = new User(firstName, lastName, email,parola);
                user.setId(id);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Iterable<Friendship> friendshipsOfAnUser(User user){
        Set<Friendship> friendshipsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where leftv = ? or rightv = ?")){
            statement.setLong(1, user.getId());
            statement.setLong(2, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long left = resultSet.getLong("leftv");
                Long right = resultSet.getLong("rightv");
                String date = resultSet.getString("datef");

                Friendship friendship = new Friendship(new Tuple<>(left, right), date);
                friendship.setId(id);
                friendshipsList.add(friendship);
            }
            return friendshipsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsList;
    }

    @Override
    public void removeFriendship(Long id1, Long id2) {
        String sql = "delete from friendships where leftv = ? and rightv = ? or leftv = ? and rightv = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id1);
            ps.setLong(2, id2);
            ps.setLong(3, id2);
            ps.setLong(4, id1);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> friendsOfAnUser(User user) {
        List<User> friendshipsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where leftv = ? or rightv = ?")){
            statement.setLong(1, user.getId());
            statement.setLong(2, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long left = resultSet.getLong("leftv");
                Long right = resultSet.getLong("rightv");

                User friend = null;
                if(left.equals(user.getId())){
                    friend = getUserFromUsers(right);
                }
                else{
                    friend = getUserFromUsers(left);
                }
                friendshipsList.add(friend);
            }
            return friendshipsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsList;
    }

    @Override
    public Page<User> friendsOfAnUser(Pageable<User> pageable, User user) {
        List<User> friendshipsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where leftv = ? or rightv = ? LIMIT (?) OFFSET (?)")){
            statement.setLong(1, user.getId());
            statement.setLong(2, user.getId());
            statement.setLong(3, pageable.getPageSize());
            statement.setLong(4, pageable.getPageSize()*(pageable.getPageNumb()-1));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long left = resultSet.getLong("leftv");
                Long right = resultSet.getLong("rightv");

                User friend = null;
                if(left.equals(user.getId())){
                    friend = getUserFromUsers(right);
                }
                else{
                    friend = getUserFromUsers(left);
                }
                friendshipsList.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PageImpl<>(pageable, friendshipsList);
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
    public Friendship findOneByEmail(String email) {
        return null;
    }

    @Override
    public Friendship findOneByParola(String parola) {
        return null;
    }

    @Override
    public void removeFriendRequest(String email1, String email2) {}

    @Override
    public List<Friendship> conversation(String email1, String email2) {
        return null;
    }

    @Override
    public Iterable<Friendship> getReqByEmail(String email) {
        return null;
    }

    @Override
    public Iterable<Cerere> getSentReqs(String email) {
        return null;
    }

    @Override
    public Page<Message> conversation(Pageable<Message> pageable, String email1, String email2) {
        return null;
    }

    @Override
    public Page<Cerere> getReqByName(Pageable<Cerere> pageable, String email) {
        return null;
    }

    @Override
    public Page<Cerere> getSentReqs(Pageable<Cerere> pageable, String email) {
        return null;
    }

    @Override
    public Page<User> getAllEntities(Pageable<User> pageable) {
        return null;
    }
}
