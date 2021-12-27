package com.example.finalproject.repository.db;

import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.repository.Repository;

import java.sql.*;
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
    public Iterable<Cerere> getAllEntities() {
        Set<Cerere> cereri = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT cereri.id, cereri.status, cereri.email_sender, cereri.email_recv\n" +
                     "FROM users\n" +
                     "INNER JOIN cereri ON cereri.email_recv = users.email;");
             ResultSet resultSet = statement.executeQuery()) {
             while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String status = resultSet.getString("status");
                String email1 = resultSet.getString("email_sender");
                String email2 = resultSet.getString("email_recv");

                Cerere c = new Cerere(email1, email2,status);
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
        Iterable<Cerere> cereri = this.getAllEntities();
        for(Cerere c :cereri){
            if(c.getId().equals(cerere.getId()))
                throw new ExistanceException();
        }
        cerere.setStatus("pending");

        String sql = "insert into cereri (id, status, email_sender, email_recv ) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)){
                    ps.setInt(1, Integer.parseInt((cerere.getId().toString())));
                    ps.setString(2, (cerere).getStatus());
                    ps.setString(3, (cerere).getEmail_sender());
                    ps.setString(4, (cerere).getEmail_recv());

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
        return null;
    }

    @Override
    public Cerere delete(Long aLong) {
            Iterable<Cerere> cereres = this.getAllEntities();
            Cerere c_todel = null;
            for(Cerere cerere : cereres){
                if(cerere.getId().equals(aLong)) {
                    c_todel = cerere;
                    break;
                }
            }
            int idToDel = Integer.parseInt(aLong.toString());
            String sql = "delete from cereri where id = ?";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idToDel);
                ps.executeUpdate();
                return c_todel;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return c_todel;
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
}
