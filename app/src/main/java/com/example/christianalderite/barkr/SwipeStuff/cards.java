package com.example.christianalderite.barkr.SwipeStuff;

/**
 * Created by Christian Alderite on 3/14/2018.
 */

public class cards {
    private String petId;
    private String name;
    private String ownerId;
    private String imageUri;
    private String breed;
    private String gender;

    public cards(String petId, String name, String breed, String gender, String imageUri){
        this.petId=petId;
        this.name=name;
        this.breed =breed;
        this.imageUri = imageUri;
        this.gender = gender;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public String getPetId() {
        return petId;
    }
}
