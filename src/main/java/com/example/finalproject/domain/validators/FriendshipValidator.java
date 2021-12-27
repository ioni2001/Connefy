package com.example.finalproject.domain.validators;


import com.example.finalproject.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String val = "";
        if(!entity.getTuple().getLeft().toString().matches("[0-9]+"))
            val = val.concat("Invalid first ID! ");
        if(!entity.getTuple().getLeft().toString().matches("[0-9]+"))
            val = val.concat("Invalid second ID! ");
        if(!val.isEmpty())
            throw new ValidationException(val);
    }
}
