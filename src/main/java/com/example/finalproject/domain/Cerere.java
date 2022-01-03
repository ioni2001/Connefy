package com.example.finalproject.domain;

import com.example.finalproject.utils.Constants;

import java.time.LocalDateTime;

public class Cerere extends Entity<Long> {
    private String email_sender;
    private String email_recv;
    private String status;
    private String date;

    public Cerere(String email_sender, String email_recv,String status, String date) {
        this.email_sender = email_sender;
        this.email_recv = email_recv;
        this.status = status;
        this.date = date;
    }

    public void setEmail_sender(String email_sender) {
        this.email_sender = email_sender;
    }

    public void setEmail_recv(String email_recv) {
        this.email_recv = email_recv;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
    }

    public String getEmail_sender() {
        return email_sender;
    }

    public String getEmail_recv() {
        return email_recv;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Cerere{" +
                "email_sender='" + email_sender + '\'' +
                ", email_recv='" + email_recv + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}

