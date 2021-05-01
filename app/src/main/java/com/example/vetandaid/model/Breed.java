package com.example.vetandaid.model;

public class Breed {
    private String Name;
    private String PicUrl;

    public Breed(){}

    public Breed(String Name, String PicUrl) {
        this.Name = Name;
        this.PicUrl = PicUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String PicUrl) {
        this.PicUrl = PicUrl;
    }
}
