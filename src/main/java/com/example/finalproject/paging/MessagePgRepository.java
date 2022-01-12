package com.example.finalproject.paging;

import com.example.finalproject.domain.Message;
import com.example.finalproject.domain.User;

public interface MessagePgRepository {
    Page<Message> conversation(Pageable<Message> pageable, String email1, String email2 );
}
