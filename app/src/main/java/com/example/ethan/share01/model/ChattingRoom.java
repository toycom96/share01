package com.example.ethan.share01.model;

/**
 * Created by OHRok on 2016-07-27.
 */
public class ChattingRoom {
    private int mChatRoomID;
    private int mRecv_id;
    private String mMsg;
    private String mSended;

    public ChattingRoom(int chatRoomID, int recv_id, String msg, String sended) {
        mChatRoomID = chatRoomID;
        mRecv_id = recv_id;
        mMsg = msg;
        mSended = sended;
    }

    public int getChatRoomID() {
        return mChatRoomID;
    }

    public void setChatRoomID(int chatRoomID) {
        mChatRoomID = chatRoomID;
    }

    public int getRecv_id() {
        return mRecv_id;
    }

    public void setRecv_id(int recv_id) {
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
