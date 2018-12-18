package com.example.acer.transitions_everywhere.models;

/**
 * Created by Mirlan on 27.10.2016.
 */

public class UserModel {

    private String userName, email, photoURL;

    public UserModel() {

    }

    public UserModel(String userName, String email, String photoURL) {
        this.userName = userName;
        this.email = email;
        this.photoURL = photoURL;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
