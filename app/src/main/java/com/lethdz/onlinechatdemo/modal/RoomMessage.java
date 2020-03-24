package com.lethdz.onlinechatdemo.modal;

public class RoomMessage {
    private String id;
    private String owner;
    private String message;
    private String timeStamp;

    public RoomMessage() {
    }

    public RoomMessage(String id, String owner, String message, String timeStamp) {
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
