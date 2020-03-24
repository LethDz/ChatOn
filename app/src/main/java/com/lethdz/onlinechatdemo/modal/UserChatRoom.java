package com.lethdz.onlinechatdemo.modal;

public class UserChatRoom {
    private String collectionName;
    private String title;
    private String lastMessage;
    private String timeStamp;

    public UserChatRoom() {
    }

    public UserChatRoom(String collectionName, String title, String lastMessage, String timeStamp) {
        this.collectionName = collectionName;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
