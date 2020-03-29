package com.lethdz.onlinechatdemo.modal;

import com.google.firebase.Timestamp;

public class UserChatRoom {
    private String documentName;
    private String title;
    private String lastMessage;
    private Timestamp timeStamp;

    public UserChatRoom() {
    }

    public UserChatRoom(String documentName, String title, String lastMessage, Timestamp timeStamp) {
        this.documentName = documentName;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
