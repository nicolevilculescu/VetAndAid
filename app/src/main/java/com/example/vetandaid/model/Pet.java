package com.example.vetandaid.model;

public class Pet {
    private String species;
    private String breed;
    private String name;
    private String birthdate;
    private String url;
    private String ownerId;

    public Pet() {}

    public Pet(String species, String breed, String name, String birthdate, String url, String ownerId) {
        this.species = species;
        this.breed = breed;
        this.name = name;
        this.birthdate = birthdate;
        this.url = url;
        this.ownerId = ownerId;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
