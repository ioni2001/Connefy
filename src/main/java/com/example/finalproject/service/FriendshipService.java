package com.example.finalproject.service;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;
import com.example.finalproject.utils.Constants;

import java.time.LocalDateTime;
import java.util.*;

public class FriendshipService  <ID, E extends Entity<ID>> implements Service<ID, E> {

    private Repository<ID, E> friendshipRepository;

    public FriendshipService(Repository<ID, E> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public ID getCurrentId() {
        return friendshipRepository.getCurrentId();
    }

    @Override
    public void setCurrentUserId(ID id) {
        friendshipRepository.setCurrentId(id);
    }

    @Override
    public Long findFistFreeId() {
        Iterable<E> allFriendships = friendshipRepository.getAllEntities();
        Long userCount = friendshipRepository.getEntitiesCount();
        for(Long possibleID = 0L; possibleID < userCount ; possibleID++){
            boolean ok = true;
            for(E user:allFriendships){
                if(user.getId().equals(possibleID)){
                    ok = false;
                    break;
                }
            }
            if(ok)
                return possibleID;
        }
        return friendshipRepository.getEntitiesCount();
    }

    @Override
    public Iterable<E> getAll() {
        return friendshipRepository.getAllEntities();
    }

    @Override
    public void add(E e) {
        Tuple<Long, Long> tuple = new Tuple<>((Long) getCurrentId(), (Long) e.getId());
        Friendship friendship = new Friendship(tuple, LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        friendship.setId(this.findFistFreeId());
        this.friendshipRepository.save((E)friendship);
    }

    @Override
    public void remove(E e) {
        Iterable<Friendship> allFriendships = (Iterable<Friendship>) friendshipRepository.getAllEntities();
        boolean ok = false;
        for (Friendship friendship : allFriendships) {
            if ((friendship.getTuple().getLeft().equals(e.getId()) && friendship.getTuple().getRight().equals(this.getCurrentId()))
                    || (friendship.getTuple().getRight().equals(e.getId()) && friendship.getTuple().getLeft().equals(this.getCurrentId()))) {
                this.friendshipRepository.delete((ID) friendship.getId());
                ok = true;
                break;
            }
        }
        if(!ok)
            throw new NotExistanceException();
    }

    @Override
    public void update(E e) {
        Iterable<Friendship> friendships = (Iterable<Friendship>) friendshipRepository.getAllEntities();
        boolean ok = false;
        for(Friendship friendship:friendships){
            if(friendship.getTuple().getLeft().equals(((Friendship)e).getTuple().getLeft()) && friendship.getTuple().getRight().equals(((Friendship)e).getTuple().getRight()) || friendship.getTuple().getRight().equals(((Friendship)e).getTuple().getLeft()) && friendship.getTuple().getLeft().equals(((Friendship)e).getTuple().getRight())){
                friendship.setDate();
                friendshipRepository.update((E) friendship);
                ok = true;
                break;
            }
        }
        if(!ok)
            throw new NotExistanceException();
    }


    /**
     * removes all the friendships that contains the entity that must be deleted
     * @param e - entity
     * */
    public void removeAll(E e){
        Iterable<Friendship> allFriendships = (Iterable<Friendship>) friendshipRepository.getAllEntities();
        List<Long> idToDel = new ArrayList<>();
        for(Friendship friendship:allFriendships){
            if(friendship.getTuple().getLeft().equals(e.getId()) || friendship.getTuple().getRight().equals(e.getId())) {
                idToDel.add(friendship.getId());
            }
        }
        for(Long id:idToDel){
            friendshipRepository.delete((ID) id);
        }
    }

    public int numberOfCommunities(List<Long> IDs){
        Collections.sort(IDs);
        Networking networking = new Networking((Iterable<Friendship>) friendshipRepository.getAllEntities(), IDs.get(IDs.size()-1) + 1, IDs);
        networking.setFriendships();
        return networking.numberOfCommunities();
    }

    public List<Long> mostSociableCommunity(List<Long> IDs){
        Collections.sort(IDs);
        Networking networking = new Networking((Iterable<Friendship>) friendshipRepository.getAllEntities(), IDs.get(IDs.size()-1) + 1, IDs);
        networking.setFriendships();
        return networking.mostSociableCommunity();
    }
}
