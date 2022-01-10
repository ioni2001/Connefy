package com.example.finalproject.service;

import com.example.finalproject.domain.*;
import com.example.finalproject.domain.validators.exceptions.NotExistanceException;
import com.example.finalproject.repository.Repository;
import com.example.finalproject.repository.memory.FriendshipMemoryRepository;
import com.example.finalproject.utils.Constants;

import java.time.LocalDateTime;
import java.util.*;

public class FriendshipService  <ID, E extends Entity<ID>> extends Observable implements Service<ID, E>{

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
        setChanged();
        notifyObservers(Friendship.class);
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
        setChanged();
        notifyObservers(Friendship.class);
    }

    @Override
    public void remove(E e) {
        this.friendshipRepository.removeFriendship(this.getCurrentId(), e.getId());
        setChanged();
        notifyObservers(Friendship.class);
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
        setChanged();
        notifyObservers(Friendship.class);
    }


    /**
     * removes all the friendships that contains the entity that must be deleted
     * @param e - entity
     * */
    public void removeAll(E e){
        Iterable<Friendship> allFriendships = (Iterable<Friendship>) this.friendshipRepository.friendshipsOfAnUser((User)e);

        for(Friendship friendship:allFriendships){
            friendshipRepository.delete((ID) friendship.getId());
        }
        setChanged();
        notifyObservers(Friendship.class);
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
    public Iterable<Friendship> getFriends(User user){
        return (Iterable<Friendship>) this.friendshipRepository.friendshipsOfAnUser(user);
    }
}
