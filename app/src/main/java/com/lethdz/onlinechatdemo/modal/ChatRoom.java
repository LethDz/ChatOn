package com.lethdz.onlinechatdemo.modal;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoom {
    private String id;
    private String title;
    private String lastMessage;
    private Timestamp timeStamp;
    private List<UserDetail> members;
    private List<RoomMessage> roomMessages;

    public ChatRoom() {
    }

    public ChatRoom(String id, String title, String lastMessage, Timestamp timeStamp, List<UserDetail> members, List<RoomMessage> roomMessages) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.members = members;
        this.roomMessages = roomMessages;
    }

    public ChatRoom(String id, String title, String lastMessage, Timestamp timeStamp, List<UserDetail> members) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<UserDetail> getMembers() {
        return members;
    }

    public void setMembers(List<UserDetail> members) {
        this.members = members;
    }

    public List<RoomMessage> getRoomMessages() {
        return roomMessages;
    }

    public void setRoomMessages(List<RoomMessage> roomMessages) {
        this.roomMessages = roomMessages;
    }
}
