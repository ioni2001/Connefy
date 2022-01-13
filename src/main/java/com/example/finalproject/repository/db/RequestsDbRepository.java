package com.example.finalproject.repository.db;

import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.Message;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.PageImpl;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestsDbRepository implements Repository<Long,Cerere> {
    private String url;
    private String username;
    private String password;
    private Object currentId;

    public RequestsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.currentId = -1L;
    }

    @Override
    public Iterable<Cerere> getSentReqs(String email){
        List<Cerere> cereri = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM cereri WHERE email_sender = ?");){
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");
                String data = resultSet.getString("data");

                Cerere c = new Cerere(email1, email2,status,data);
                c.setId(id);
                cereri.add(c);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return cereri;
    }

    @Override
    public Iterable<Cerere> getAllEntities() {
        List<Cerere> cereri = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT cereri.id, cereri.status, cereri.email_sender, cereri.email_recv, cereri.data\n" +
                     "FROM users\n" +
                     "INNER JOIN cereri ON cereri.email_sender = users.email ;");){
             ResultSet resultSet = statement.executeQuery();
             while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");
                String data = resultSet.getString("data");

                Cerere c = new Cerere(email1, email2,status,data);
                c.setId(id);
                cereri.add(c);
             }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return cereri;
    }

    @Override
    public Cerere update(Cerere cerere){
        String sql = "update cereri set status = ? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cerere.getStatus());
            ps.setInt(2, Integer.parseInt((cerere.getId().toString())));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cerere;
    }

    @Override
    public Cerere save(Cerere cerere){

        String sql2 = "SELECT * FROM cereri WHERE email_sender = ? and email_recv = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql2)){
             statement.setString(1, cerere.getEmail_sender());
             statement.setString(2, cerere.getEmail_recv());
             ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
               throw new ExistanceException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        cerere.setStatus("pending");

        String sql = "insert into cereri (id, status, email_sender, email_recv, data ) values (?, ?, ?, ?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)){
                    ps.setInt(1, Integer.parseInt((cerere.getId().toString())));
                    ps.setString(2, (cerere).getStatus());
                    ps.setString(3, (cerere).getEmail_sender());
                    ps.setString(4, (cerere).getEmail_recv());
                    ps.setString(5, (cerere).getDate());

            ps.executeUpdate();
            return cerere;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long getEntitiesCount(){
        String sql = "SELECT COUNT(email_sender) FROM cereri";
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
    public Long getCurrentId() {
        return null;
    }

    @Override
    public void setCurrentId(Long currentId) {

    }

    @Override
    public Cerere findOne(Long aLong) {
        String sql = "SELECT * FROM cereri WHERE id= ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
             statement.setLong(1, aLong);
             ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");
                String data = resultSet.getString("data");

                Cerere c = new Cerere(email1, email2,status,data);
                c.setId(id);
                return c;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cerere delete(Long aLong) {
            int idToDel = Integer.parseInt(aLong.toString());
            String sql = "delete from cereri where id = ?";
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
    public String getCurrentEmail() {
        return null;
    }

    @Override
    public List<Long> getAllIDs() {
        return null;
    }

    @Override
    public Cerere findOneByEmail(String email) {
        return null;
    }

    @Override
    public Cerere findOneByParola(String parola) {
        return null;
    }

    @Override
    public Iterable<Cerere> friendshipsOfAnUser(User e) {
        return null;
    }

    @Override
    public void removeFriendship(Long id1, Long id2) {

    }

    @Override
    public void removeFriendRequest(String email1, String email2) {
        String sql = "delete from cereri where email_sender = ? and email_recv = ? or email_sender = ? and email_recv = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email1);
            ps.setString(2, email2);
            ps.setString(3, email2);
            ps.setString(4, email1);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cerere> conversation(String email1, String email2) {
        return null;
    }

    @Override
    public Iterable<Cerere> getReqByEmail(String email) {
        Set<Cerere> cereri = new HashSet<>();
        String sql = "SELECT * FROM cereri WHERE email_recv= ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");
                String data = resultSet.getString("data");

                Cerere c = new Cerere(email1, email2,status,data);
                c.setId(id);
                cereri.add(c);
            }
            return cereri;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cereri;
    }

    @Override
    public Page<Friendship> friendshipsOfAnUser(Pageable<Friendship> pageable, User user) {
        return null;
    }

    @Override
    public Page<Message> conversation(Pageable<Message> pageable, String email1, String email2) {
        return null;
    }

    @Override
    public Page<Cerere> getReqByName(Pageable<Cerere> pageable, String email) {
        List<Cerere> cereri = new ArrayList<>();
        String sql = "SELECT * FROM cereri WHERE email_recv= ? LIMIT (?) OFFSET (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, email);
            statement.setLong(2, pageable.getPageSize());
            statement.setLong(3, pageable.getPageSize()*(pageable.getPageNumb()-1));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");
                String data = resultSet.getString("data");

                Cerere c = new Cerere(email1, email2,status,data);
                c.setId(id);
                cereri.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PageImpl<>(pageable,cereri);
    }

    @Override
    public Page<User> getAllEntities(Pageable<User> pageable) {
        return null;
    }
}
