package com.example.finalproject.service;

import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.Friendship;
import com.example.finalproject.paging.Page;
import com.example.finalproject.paging.Pageable;
import com.example.finalproject.repository.Repository;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class RequestsService<ID,E extends Entity<ID>> extends Observable implements Service<ID,E> {

    private Repository<ID, E> requestRepo;

    public RequestsService(Repository<ID,E> r) {
        this.requestRepo = r;
    }

    @Override
    public ID getCurrentId() {
        return this.requestRepo.getCurrentId();
    }

    @Override
    public void setCurrentUserId(ID id) {

    }

    @Override
    public Long findFistFreeId() {
        Iterable<Cerere> req = (Iterable<Cerere>) requestRepo.getAllEntities();
        Long count = requestRepo.getEntitiesCount();
        for(Long possibleID = 0L; possibleID < count ; possibleID++){
            boolean ok = true;
            for(Cerere c:req){
                if(c.getId().equals(possibleID)){
                    ok = false;
                    break;
                }
            }
            if(ok)
                return possibleID;
        }
        return requestRepo.getEntitiesCount();
    }

    public Iterable<Cerere> getRequestsByEmail(String email){
        return (Iterable<Cerere>) this.requestRepo.getReqByEmail(email);
    }

    public Iterable<Cerere> getSentReqs(String email){
        return this.requestRepo.getSentReqs(email);
    }

    public Page<Cerere> getSentReqs(Pageable<Cerere> pageable, String email){
        return this.requestRepo.getSentReqs(pageable, email);
    }


    @Override
    public Iterable<E> getAll() {
        return this.requestRepo.getAllEntities();
    }

    @Override
    public void add(E entity) {
        entity.setId((ID) findFistFreeId());
        requestRepo.save(entity);
        setChanged();
        notifyObservers(Cerere.class);
    }

    @Override
    public void remove(E entity) {
        this.requestRepo.delete(entity.getId());
        setChanged();
        notifyObservers(Cerere.class);
    }

    @Override
    public void update(E entity) {
        this.requestRepo.update(entity);
        setChanged();
        notifyObservers(Cerere.class);
    }

    public Page<Cerere> getReqByName(Pageable<Cerere> pageable, String email){
        return this.requestRepo.getReqByName(pageable,email);
    }

    public void removeCerere(String email1, String email2){
        this.requestRepo.removeFriendRequest(email1, email2);
        setChanged();
        notifyObservers(Cerere.class);
    }
}
