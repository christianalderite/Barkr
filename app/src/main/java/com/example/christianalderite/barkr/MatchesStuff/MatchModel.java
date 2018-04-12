package com.example.christianalderite.barkr.MatchesStuff;

import com.example.christianalderite.barkr.PetStuff.PetModel;

public class MatchModel {

    public MatchModel(){
    }

    String matchId, matchOwner, matchName, earliestMessage, matchUri;

    public MatchModel(PetModel pet, String earliestMessage){
        this.matchId = pet.getPetId();
        this.matchOwner = pet.getOwnerDisplayName();
        this.earliestMessage = earliestMessage;
        this.matchName = pet.getName();
        this.matchUri = pet.getPetImageUri();
    }

    public String getEarliestMessage() {
        return earliestMessage;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchOwner() {
        return matchOwner;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getMatchUri() {
        return matchUri;
    }
}
