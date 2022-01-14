package com.example.finalproject.service;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;
import com.example.finalproject.utils.Constants;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MessageService  <ID, E extends Entity<ID>> extends Observable implements Service<ID, E> {

    private Repository<ID, E> messageRepository;

    public MessageService(Repository<ID, E> messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void add(E e) {
        this.messageRepository.save(e);
        setChanged();
        notifyObservers(Message.class);
    }

    public Long size(){
        return messageRepository.getEntitiesCount();
    }

    @Override
    public void remove(E e) {

    }

    @Override
    public void update(E e) {
    }
    @Override
    public ID getCurrentId() {
        return null;
    }

    @Override
    public void setCurrentUserId(ID id) {
    }

    @Override
    public Long findFistFreeId() {
        return null;
    }

    @Override
    public Iterable<E> getAll() {
        return messageRepository.getAllEntities();
    }

    public Message findOne(ID id){
        try {
            return (Message) this.messageRepository.findOne(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> conversation(String email1, String email2){
        return (List<Message>) this.messageRepository.conversation(email1, email2);
    }

    public Page<Message> conversation(Pageable<Message> pageable, String email1, String email2){
        return this.messageRepository.conversation(pageable, email1, email2);
    }
}
