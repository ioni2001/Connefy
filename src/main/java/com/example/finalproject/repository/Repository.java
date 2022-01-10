package com.example.finalproject.repository;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @return the number of entities
     */
    public Long getEntitiesCount();

    /**
     * @return the if of the current entity
     */
    public ID getCurrentId();

    /**
     * Sets the current id with the id of the current entity
     *
     * @param currentId the id of the current entity
     */
    public void setCurrentId(ID currentId);


    /**
     * Returns an entity by di
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOne(ID id) throws SQLException;

    /**
     * @return all entities
     */
    Iterable<E> getAllEntities();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E save(E entity);


    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    E delete(ID id);

    /**
     * updates an entity
     *
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    E update(E entity);

    public String getCurrentEmail();

    public List<Long> getAllIDs();

    public E findOneByEmail(String email);

    E findOneByParola(String parola);
    public Iterable<E> friendshipsOfAnUser(User e);
    public void removeFriendship(ID id1, ID id2);
    public void removeFriendRequest(String email1, String email2);
}

