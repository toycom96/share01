package com.example.ethan.share01;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListObject
{
    private int mId;
    private int mUserId;
    private String mUser;
    private String mPicUrl;
    private int mTime;
    private String mEtc;
    private String mMsg;
    private String mSex;
    private int mAge;
    private int mDist;

    public ContentsListObject(int Id, int UserId, String User, String PicUrl, String Time, String Etc, String Msg, String Sex, int Age, int Dist)
    {
        this.mId = Id;
        this.mUserId = UserId;
        this.mUser = User;
        this.mPicUrl = PicUrl;
        this.mTime = Integer.parseInt(Time);
        this.mEtc = Etc;
        this.mMsg = Msg;
        this.mSex = Sex;
        this.mAge = Age;
        this.mDist = Dist;
    }

    public int getId() { return mId; }
    public void setId(int Id) { this.mId = Id; }

    public int getUserId() { return mUserId; }
    public void setUserId(int UserId) { this.mUserId = UserId; }

    public String getUser() { return mUser; }
    public void setUser(String User) {
        this.mUser = User;
    }

    public String getPicUrl()
    {
        return mPicUrl;
    }
    public void setPicUrl(String PicUrl) { this.mPicUrl = PicUrl; }

    public int getTime()
    {
        return mTime;
    }
    public void setTime(int Time) { this.mTime = mTime; }

    public String getEtc() { return mEtc; }
    public void setEtc(String Etc) { this.mEtc = Etc; }

    public String getMsg() { return mMsg; }
    public void setMsg(String Msg) { this.mMsg = Msg; }

    public String getSex()
    {
        return mSex;
    }
    public void setSex(String Sex) { this.mSex = Sex; }

    public int getAge()
    {
        return mAge;
    }
    public void setAge(int Age) { this.mAge = Age; }

    public int getDist()
    {
        return mDist;
    }
    public void setDist(int Dist) { this.mDist = Dist; }

}