package com.example.christianalderite.barkr.PetStuff;



public class PetModel {

    private String name, breed, birthdate, gender, others, petId, ownerId, ownerDisplayName;
    private String petImageUri;

    public PetModel(){

    }

    public PetModel(String petId, String ownerId, String name, String ownerDisplayName, String breed, String birthdate, String gender, String others, String petUri){
        this.petId = petId;
        this.ownerId=ownerId;
        this.name = name;
        this.ownerDisplayName = ownerDisplayName;
        this.breed = breed;
        this.birthdate = birthdate;
        this.gender = gender;
        this.others = others;
        this.petImageUri = petUri;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public String getPetId(){
        return petId;
    }

    public String getPetImageUri() {
        return petImageUri;
    }

    public void setPetImageUri(String petImageUri) {
        this.petImageUri = petImageUri;
    }

    public void setPetId(String petId){
        this.petId=petId;
    }

    public String getName(){
        return name;
    }

    public String getBreed(){
        return breed;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getOthers() {
        return others;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
