package com.example.finalproject.paging;

import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.User;

public interface FriendshipsPgRepository {
    Page<User> friendsOfAnUser(Pageable<User> pageable, User user);
}
