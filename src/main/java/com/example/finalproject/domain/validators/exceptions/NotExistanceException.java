package com.example.finalproject.domain.validators.exceptions;

public class NotExistanceException extends RuntimeException{
    public NotExistanceException(){
        super("Doesn't exists!");
    }
}