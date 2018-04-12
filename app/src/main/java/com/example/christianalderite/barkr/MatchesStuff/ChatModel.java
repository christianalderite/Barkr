package com.example.christianalderite.barkr.MatchesStuff;

public class ChatModel {

    private String message;
    private Boolean currentPet;

    public ChatModel(String message, Boolean currentPet){
        this.message = message;
        this.currentPet = currentPet;
    }

    public String getMessage(){
        return this.message;
    }

    public Boolean getCurrentPet() {
        return this.currentPet;
    }
}
