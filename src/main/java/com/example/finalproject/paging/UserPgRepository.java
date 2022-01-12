package com.example.finalproject.paging;

import com.example.finalproject.domain.User;

public interface UserPgRepository {
    Page<User> getAllEntities(Pageable<User> pageable);
}
