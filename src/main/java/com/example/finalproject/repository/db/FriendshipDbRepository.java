package com.example.finalproject.repository.db;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.Tuple;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
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
    public Iterable<Friendship> friendshipsOfAnUser(User user){
        Set<Friendship> friendshipsList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where leftv = '" + user.getId() + "' or rightv = '" + user.getId() + "'");
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
    public void removeFriendship(Long id1, Long id2) {
        String sql = "delete from friendships where leftv = '"+ id1 +"' and rightv = '"+ id2 +"' or leftv = '"+ id2 +"' and rightv = '"+ id1 +"'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFriendRequest(String email1, String email2) {

    }


}
