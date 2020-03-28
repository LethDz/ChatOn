package com.lethdz.onlinechatdemo.modal;

import com.google.firebase.Timestamp;

public class RoomMessage {
    private String id;
    private String owner;
    private String message;
    private Timestamp timeStamp;

    public RoomMessage() {
    }

    public RoomMessage(String id, String owner, String message, Timestamp timeStamp) {
        this.id = id;
        this.owner = owner;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
