package com.example.finalproject.domain;

public class Cerere extends Entity<Long> {
    private String email_sender;
    private String email_recv;
    private String status;

    public Cerere(String email_sender, String email_recv,String status) {
        this.email_sender = email_sender;
        this.email_recv = email_recv;
        this.status = status;
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

