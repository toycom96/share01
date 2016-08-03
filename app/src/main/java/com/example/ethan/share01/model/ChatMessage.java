package com.example.ethan.share01.model;

/**
 * Created by Lai.OH on 2016-07-28.
 */
public class ChatMessage {
    private int msg_id;
    private int room_id;
    private int sender_id;
    private String sender_name;
    private String message;
    private String time;

    public ChatMessage(int msg_id, int room_id, int sender_id, String sender_name, String message, String time) {
        this.msg_id = msg_id;
        this.room_id = room_id;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.message = message;
        this.time = time;
    }
    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
