package com.example.finalproject.ui;

import com.example.finalproject.domain.Entity;

public interface UI<ID,E extends Entity<ID>, ID2, E2 extends Entity<ID2>, ID3, E3 extends Entity<ID3>, ID4, E4 extends Entity<ID4>> {

    void startUI();
    void printAll();
    void logIn();
    void addUser();
    void removeUser();
    void addFriend();
    void removeFriend();
    void numberOfCommunities();
    void mostSociableCommunity();
    void updateUser();
    void updateFriendship();
    void findOneByEmail();
    void lab5_1();
    void friendshipsByMonth();
    void sendMessage();
    void replyMessage();
    void viewConversation();
    void lab5_4();
    void replyAll();
}
