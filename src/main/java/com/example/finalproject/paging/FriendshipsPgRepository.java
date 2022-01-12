package com.example.finalproject.paging;

import com.example.finalproject.domain.Friendship;
import com.example.finalproject.domain.User;

public interface FriendshipsPgRepository {
    Page<Friendship> friendshipsOfAnUser(Pageable<Friendship> pageable, User user);
}
