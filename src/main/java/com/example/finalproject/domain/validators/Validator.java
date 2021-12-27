package com.example.finalproject.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}