package com.example.finalproject.domain.validators;

import com.example.finalproject.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String val = "";
        if(entity.getFirstName().isEmpty() || !entity.getFirstName().matches("[a-zA-Z]+") || (entity.getFirstName().charAt(0) < 'A' || entity.getFirstName().charAt(0) > 'Z')){
            val = val.concat("Invalid firstname! ");
        }
        if(entity.getLastName().isEmpty() || !entity.getLastName().matches("[a-zA-Z]+") || (entity.getLastName().charAt(0) < 'A' || entity.getLastName().charAt(0) > 'Z')){
            val = val.concat("Invalid lastname! ");
        }
        if(entity.getEmail().isEmpty() || !entity.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            val = val.concat("Invalid email! ");
        }
        if(!val.isEmpty())
            throw new ValidationException(val);
    }
}
