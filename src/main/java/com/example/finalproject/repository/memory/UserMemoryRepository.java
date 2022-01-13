package com.example.finalproject.repository.memory;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.IdNullException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.repository.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    private Map<ID, E> entities;
    private Object currentId;

    public UserMemoryRepository(){

    }
    public UserMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
        this.currentId = -1;
    }

    @Override
    public Long getEntitiesCount() {
        return Long.valueOf(entities.size());
    }

    @Override
    public ID getCurrentId() {
        return (ID) currentId;
    }

    /**
     * @return String - the email of the current entity if the current id exists in memory
     *         null - otherwise
     * */
    @Override
    public String getCurrentEmail() {

        Iterable<User> entities = (Iterable<User>) getAllEntities();
        for (User user : entities) {
            if (user.getId().equals(getCurrentId())) {
                return user.getEmail();
            }
        }
        return null;
    }
    @Override
    public void setCurrentId(ID currentId) {
        this.currentId = currentId;
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IdNullException();
        return entities.get(id);
    }

    /**
     * @return List of long that represents the ids of the users in memory
     * */
    @Override
    public List<Long> getAllIDs(){
        List<Long> rez = new ArrayList<>();
        Iterable<ID>keys = entities.keySet();
        for(ID id:keys)
            rez.add((Long) id);
        return rez;
    }

    @Override
    public Iterable<E> getAllEntities() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        User user = (User) entity;
        Iterable<E> users = this.getAllEntities();
        for(E e:users){
            if(((User) e).getEmail().equals(user.getEmail()))
                throw new ExistanceException();
        }
        if (entities.get(entity.getId()) != null) {
            return entity;
        } else entities.put(entity.getId(), entity);
        return null;
    }

    @Override
    public E delete(ID id){

        if (id == null) {
            throw new IdNullException();
        }

        E entity = entities.get(id);
        if (entity == null) {
            throw new NotExistanceException();
        }

        entities.remove(id);
        return entity;
    }


    @Override
    public E update(E entity) {

        if (entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        entities.put(entity.getId(), entity);

        if (entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), entity);
            return null;
        }
        return entity;

    }

    @Override
    public E findOneByEmail(String email){
        return null;
    }

    @Override
    public E findOneByParola(String parola) {
        return null;
    }

    @Override
    public Iterable<E> friendshipsOfAnUser(User e) {
        return null;
    }

    @Override
    public void removeFriendship(ID id1, ID id2) {

    }

    @Override
    public void removeFriendRequest(String email1, String email2) {

    }

    @Override
    public List<E> conversation(String email1, String email2) {
        return null;
    }

    @Override
    public Iterable<E> getReqByEmail(String email) {
        return null;
    }

    @Override
    public Iterable<Cerere> getSentReqs(String email) {
        return null;
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
        return null;
    }

    @Override
    public Page<User> getAllEntities(Pageable<User> pageable) {
        return null;
    }
}
