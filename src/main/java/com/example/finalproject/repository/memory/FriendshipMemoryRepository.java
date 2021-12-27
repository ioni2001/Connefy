package com.example.finalproject.repository.memory;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.Validator;
import com.example.finalproject.domain.validators.exceptions.EntityNullException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.IdNullException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendshipMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    private Map<ID, E> entities;
    private Object currentId;

    public FriendshipMemoryRepository(){

    }
    public FriendshipMemoryRepository(Validator<E> validator) {
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

    @Override
    public Iterable<E> getAllEntities() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        Iterable<E> friendships = this.getAllEntities();
        for(E e:friendships){
            if((((Friendship)e).getTuple().getLeft().equals(((Friendship)entity).getTuple().getLeft()) && ((Friendship)e).getTuple().getRight().equals(((Friendship)entity).getTuple().getRight())) ||(((Friendship)e).getTuple().getLeft().equals(((Friendship)entity).getTuple().getRight()) && ((Friendship)e).getTuple().getRight().equals(((Friendship)entity).getTuple().getLeft()))){
                throw new ExistanceException();
            }
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

        if(entity == null)
            throw new EntityNullException();
        validator.validate(entity);

        entities.put(entity.getId(),entity);

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
    public E findOneByEmail(String email) {
        return null;
    }

    /**
     * Clears the container where the entities are stored
     * */
    public void clearList(){
        this.entities.clear();
    }
}
