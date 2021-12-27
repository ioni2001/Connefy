package com.example.finalproject.service;

import com.example.finalproject.domain.Entity;
import com.example.finalproject.domain.validators.ValidationException;

public interface Service <ID,E extends Entity<ID>> {

    /**
     * @return the if of the current entity
     * */
    public ID getCurrentId();

    /**
     * Sets the current id with the id of the current entity
     * @param id the id of the current entity
     * */
    public void setCurrentUserId(ID id);


    /**
     * search in memory for a free id
     * @return Long - the id
     * */
    public Long findFistFreeId();

    /**
     * @return a list with all the entities in memory
     * */
    Iterable<E> getAll();

    /**
     * saves in memory the entity
     * @param e
     *         entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
        */
    void add(E e);

    /**
     *  removes the entity with the specified id
     * @param e
     *      entity must be not null
     */
    void remove(E e);

    void update(E e);

}
