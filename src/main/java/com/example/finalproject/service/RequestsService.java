package com.example.finalproject.service;

import com.example.finalproject.domain.Cerere;
import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.Friendship;
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
        Set<Cerere> cereri = new HashSet<>();
        Iterable<Cerere> cereres = (Iterable<Cerere>) requestRepo.getAllEntities();
        for(Cerere c: cereres){
            if(c.getEmail_recv().equals(email)){
                cereri.add(c);
            }
        }
        return cereri;
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
}
