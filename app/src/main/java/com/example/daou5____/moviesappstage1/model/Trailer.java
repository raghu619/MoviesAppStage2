package com.example.daou5____.moviesappstage1.model;

import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName( "key" )
    private String key;

    @SerializedName( "name" )
    private String name;

    public Trailer ( String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() { return key; }

    public void setKey() { this.key = key; }

    public  String getName() { return name; }

    public void setName() { this.name = name;}
    }
