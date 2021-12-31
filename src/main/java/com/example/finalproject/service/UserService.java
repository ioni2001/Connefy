package com.example.finalproject.service;
import java.lang.Long;
import java.util.List;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.ValidationException;
import com.example.finalproject.domain.validators.exceptions.ExistanceException;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.UserMemoryRepository;

public class UserService <ID, E extends Entity<ID>> implements Service<ID, E> {

    private Repository<ID, E> userRepository;

    public UserService(Repository<ID, E> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ID getCurrentId() {
        return userRepository.getCurrentId();
    }

    public String getCurrentEmail(){ return userRepository.getCurrentEmail();}

    @Override
    public void setCurrentUserId(ID id) {
        userRepository.setCurrentId(id);
    }

    @Override
    public Iterable<E> getAll() {
        return userRepository.getAllEntities();
    }

    @Override
    public Long findFistFreeId(){
        Iterable<E> allUsers = userRepository.getAllEntities();
        Long userCount = userRepository.getEntitiesCount();
        for(Long possibleID = 0L; possibleID < userCount ; possibleID++){
            boolean ok = true;
            for(E user:allUsers){
                if(user.getId().equals(possibleID)){
                    ok = false;
                    break;
                }
            }
            if(ok)
                return possibleID;
        }
        return userRepository.getEntitiesCount();
    }


    @Override
    public void add(E e) {

        e.setId((ID)this.findFistFreeId());
        this.userRepository.save(e);
    }

    public List<Long> getAllIDs(){
        return userRepository.getAllIDs();
    }

    @Override
    public void remove(E e) {
        this.userRepository.delete(e.getId());
    }

    public User getUser(Long id){
        return (User) userRepository.findOne((ID) id);
    }

    public User getUserByEmail(String email){
        Iterable<User> allUsers = (Iterable<User>) userRepository.getAllEntities();
        for(User user: allUsers){
            if(user.getEmail().equals(email))
                return user;
        }
        throw new NotExistanceException();
    }

    @Override
    public void update(E entity){
        userRepository.update(entity);
    }

    public User findOneByEmail(String email){
        User user = (User) userRepository.findOneByEmail(email);
        if(user == null)
            throw new NotExistanceException();
        return user;
    }

    public User findOneByParola(String parola){
        User user = (User) userRepository.findOneByParola(parola);
        if(user == null)
            throw new NotExistanceException();
        return user;
    }
}
