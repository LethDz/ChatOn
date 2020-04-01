package com.lethdz.onlinechatdemo.modal;


import java.util.List;

public class UserDetail {
    private String uid;
    private String email;
    private String displayName;
    private String photoURL;
    private List<UserChatRoom> chatRoom;

    public UserDetail() {
    }

    public UserDetail(String uid, String email, String displayName, List<UserChatRoom> chatRoom) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.chatRoom = chatRoom;
    }

    public UserDetail(String uid, String email, String displayName, String photoURL) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoURL = photoURL;
    }

    public UserDetail(String uid, String email, String displayName, String photoURL, List<UserChatRoom> chatRoom) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoURL = photoURL;
        this.chatRoom = chatRoom;
    }

    public String getUid() {
        return uid;
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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public List<UserChatRoom> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(List<UserChatRoom> chatRoom) {
        this.chatRoom = chatRoom;
    }
}
