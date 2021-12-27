package com.example.finalproject.repository.file;

import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.Tuple;
import com.example.finalproject.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFileRepository extends AbstractFriendshipFileRepository<Long, Friendship>{


    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId() + ";" + entity.getTuple().getLeft() + ";" + entity.getTuple().getRight() + ";" + entity.getDate();
    }

    @Override
    protected Friendship extractEntity(List<String> atributes) {
        Friendship friendship = new Friendship(new Tuple<>(Long.parseLong(atributes.get(1)), Long.parseLong(atributes.get(2))), atributes.get(3));
        friendship.setId(Long.parseLong(atributes.get(0)));
        return friendship;
    }
}