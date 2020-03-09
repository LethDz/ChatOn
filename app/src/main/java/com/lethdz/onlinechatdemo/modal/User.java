package com.lethdz.onlinechatdemo.modal;

import android.net.Uri;

public class User {
    private String name;
    private String email;
    private Uri photoUrl;
    private boolean emailVerified;
    private String IdToken;


    public User() {
    }

    public User(String name, String email, Uri photoUrl, boolean emailVerified) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.emailVerified = emailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getIdToken() {
        return IdToken;
    }

    public void setIdToken(String idToken) {
        IdToken = idToken;
    }
}
