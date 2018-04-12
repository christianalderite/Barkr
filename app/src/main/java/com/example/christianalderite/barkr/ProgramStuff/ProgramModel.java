package com.example.christianalderite.barkr.ProgramStuff;

import java.util.ArrayList;

/*
 * Created by Christian Alderite on 3/9/2018.
 */
public class ProgramModel {

    private String programId, hostId, title, hostDisplayName, hostEmail, location, starttime, endtime, description, programImageUri;
    private int participantsCount;
    private ArrayList<String> participants;

    public ProgramModel(){
    }

    public ProgramModel(String programId, String hostId, String title, String hostDisplayName, String hostEmail, String location, String starttime, String endtime, String description, String programImageUri){
        this.programId = programId;
        this.hostId = hostId;
        this.title = title;
        this.hostDisplayName = hostDisplayName;
        this.hostEmail = hostEmail;
        this.location = location;
        this.starttime = starttime;
        this.endtime = endtime;
        this.description = description;
        this.programImageUri = programImageUri;
        this.participantsCount = 0;
    }

    public String getProgramImageUri() {
        return programImageUri;
    }

    public String getHostId() { return hostId; }

    public String getHostDisplayName() {
        return hostDisplayName;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public String getProgramId (){ return programId; }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public int getParticipantsCount(){
        return this.participantsCount;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setHostDisplayName(String hostDisplayName) {
        this.hostDisplayName = hostDisplayName;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParticipants(ArrayList<String> participants){
        this.participants = participants;
        this.participantsCount=this.participants.size();
    }

    public void setParticipantsCount(int count){
        this.participantsCount=count;
    }

    public void addParticipant(String name){
        this.participants.add(name);
        this.participantsCount=this.participants.size();
    }
}

