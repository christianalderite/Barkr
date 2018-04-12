package com.example.christianalderite.barkr.ProgramStuff;

/**
 * Created by Christian Alderite on 3/23/2018.
 */

public class ParticipantModel {

    private String participantId, imageUri, displayName;

    public ParticipantModel(){}

    public ParticipantModel(String participantId, String displayName, String imageUri){
        this.participantId = participantId;
        this.displayName = displayName;
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
