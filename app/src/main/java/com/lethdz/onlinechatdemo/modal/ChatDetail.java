package com.lethdz.onlinechatdemo.modal;

import java.util.List;

public class ChatDetail {
    private String id;
    private String title;
    private String lastMessage;
    private String timeStamp;
    private List<User> members;
    private List<RoomMessage> roomMessages;

    public ChatDetail() {
    }

    public ChatDetail(String id, String title, String lastMessage, String timeStamp, List<User> members, List<RoomMessage> roomMessages) {
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.members = members;
        this.roomMessages = roomMessages;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<RoomMessage> getRoomMessages() {
        return roomMessages;
    }

    public void setRoomMessages(List<RoomMessage> roomMessages) {
        this.roomMessages = roomMessages;
    }
}
