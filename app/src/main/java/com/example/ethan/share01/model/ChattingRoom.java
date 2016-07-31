package com.example.ethan.share01.model;

/**
 * Created by Lai.OH on 2016-07-27.
 */
public class ChattingRoom {
    private String mChatRoomID;
    private String mRecv_id;
    private String mMsg;
    private String mSended;

    public ChattingRoom(String chatRoomID, String recv_id, String msg, String sended) {
        mChatRoomID = chatRoomID;
        mRecv_id = recv_id;
        mMsg = msg;
        mSended = sended;
    }

    public String getChatRoomID() {
        return mChatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        mChatRoomID = chatRoomID;
    }

    public String getRecv_id() {
        return mRecv_id;
    }

    public void setRecv_id(String recv_id) {
        mRecv_id = recv_id;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getSended() {
        return mSended;
    }

    public void setSended(String sended) {
        mSended = sended;
    }
}
