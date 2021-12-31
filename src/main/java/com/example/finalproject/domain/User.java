package com.example.finalproject.domain;

import com.example.finalproject.utils.HashFunction;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

public class User extends Entity<Long>{

    private String firstName,lastName,email,parola;

    public User(String firstName,String lastName, String email, String parola) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.parola = parola;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "USER is  "+
                "First name : " + firstName + ", Last name :" + lastName + " , Email : " + email  ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return firstName.equals(user.firstName) && lastName.equals(user.lastName) && email.equals(user.email) && parola.equals(user.parola);
    }
}
