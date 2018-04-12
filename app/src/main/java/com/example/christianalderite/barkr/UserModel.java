package com.example.christianalderite.barkr;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * Created by Christian Alderite on 3/9/2018.
 */

public class UserModel {

    private String uid, email, password,displayName, bio, birthDate, gender, photoUri;
    private Date dateBirthDate;
    private int age;
    private FirebaseUser user;

    public UserModel(FirebaseUser user){
        this.user = user;

    }


    public UserModel(String uid, String email, String displayName, String birthDate, String gender, String bio, String photoUri){
        this.uid = uid;
        this.email = email;
        this.birthDate = birthDate;
        this.displayName = displayName;
        this.bio = bio;
        this.gender = gender;
        this.photoUri= photoUri;
    }


    public UserModel(){}

    public String getBio() {
        return bio;
    }


    public void setPhotoUri(String Uri){
        this.photoUri=Uri;
    }

    public void setDisplayName(String firstName, String lastName){
        this.displayName=firstName+" "+lastName;
    }

    public void setBirthDate(String birthDate){
        this.birthDate = birthDate;
    }

    public void setGender(String gender){
        this.gender=gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateBirthDate() {
        return dateBirthDate;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getUid(){
        return uid;
    }

    public int getAge() {
        return age;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getGender() {
        return gender;
    }


    public String getPassword() {
        return password;
    }

    public String getPhotoUri(){
        return photoUri;
    }
}
