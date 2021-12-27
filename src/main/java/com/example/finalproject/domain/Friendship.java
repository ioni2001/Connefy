package com.example.finalproject.domain;

import com.example.finalproject.utils.Constants;

import java.time.LocalDateTime;

public class Friendship extends Entity<Long>{
    private Tuple<Long, Long> tuple;
    private String date;

    public Friendship(Tuple<Long, Long> tuple, String date){
        this.tuple = tuple;
        this.date = date;
    }

    public Tuple<Long, Long> getTuple() {
        return tuple;
    }

    public void setTuple(Tuple<Long, Long> tuple) {
        this.tuple = tuple;
    }

    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return tuple.getLeft().equals(that.getTuple().getLeft()) && tuple.getRight().equals(that.getTuple().getRight());
    }


    @Override
    public String toString() {
        return "Friendship{" +
                "tuple=" + tuple +
                ',' +
                "date=" + date +
                '}';
    }
}
