package com.example.finalproject.domain.validators.exceptions;

public class IdNullException extends RuntimeException{
    public IdNullException() {
        super("ID must not be null!\n");
    }
}
