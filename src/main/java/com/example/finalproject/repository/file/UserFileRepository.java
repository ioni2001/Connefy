package com.example.finalproject.repository.file;

import com.example.finalproject.domain.User;
import com.example.finalproject.domain.validators.Validator;

import java.util.List;

public class UserFileRepository extends AbstractUserFileRepository<Long, User>{


    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getEmail();
    }

    @Override
    protected User extractEntity(List<String> atributes) {
        User user = new User(atributes.get(1), atributes.get(2), atributes.get(3),atributes.get(4));
        user.setId(Long.parseLong(atributes.get(0)));
        return user;
    }
}