package com.example.finalproject.domain;

import java.time.LocalDate;

public class Cerinte12DTO {
    private String nume;
    private String prenume;
    private String date;

    public Cerinte12DTO(String nume, String prenume, String date) {
        this.nume = nume;
        this.prenume = prenume;
        this.date = date;
    }

    @Override
    public String toString() {
        return  "Nume: " + nume +
                "| Prenume: " + prenume  +
                "| Data: " + date;
    }
}
