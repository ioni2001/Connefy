package com.example.finalproject.domain;

import java.io.Serializable;

public class Entity <ID> implements Serializable {

    protected ID id;
    public ID getId(){ return id;}
    public void setId(ID id){this.id = id;}

}
