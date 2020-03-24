package com.lethdz.onlinechatdemo.modal;

import android.net.Uri;

import java.util.List;

public class UserDetail {
    private String uid;
    private String email;
    private String displayName;
    private Uri photoURL;
    private List<UserChatRoom> chatRoom;

    public UserDetail() {
    }

    public UserDetail(String uid, String email, String displayName, List<UserChatRoom> chatRoom) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.chatRoom = chatRoom;
    }

    public UserDetail(String uid, String email, String displayName, Uri photoURL) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoURL = photoURL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(Uri photoURL) {
        this.photoURL = photoURL;
    }

    public List<UserChatRoom> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(List<UserChatRoom> chatRoom) {
        this.chatRoom = chatRoom;
    }
}
