package com.example.finalproject.domain.validators.exceptions;

public class ExistanceException extends RuntimeException{
    public ExistanceException(){
        super("Already exists!");
    }
}
