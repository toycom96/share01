package com.example.ethan.share01.model;

/**
 * Created by leedongkwang on 2016. 10. 12..
 */

public class BbsMemo {
    private int mBbs_id;
    private int mBbs_memoid;
    private int mUser_id;
    private String mUser_name;
    private int mUser_age;
    private String mUser_sex;
    private String mUser_photo;
    private String mMemo;
    private String mTerm;
    private String mDate;

    public BbsMemo(int bbs_id, int memo_id, int user_id, String user_name, int user_age, String user_sex, String user_photo, String memo, String date) {
        this.mBbs_id = bbs_id;
        this.mBbs_memoid = memo_id;
        this.mUser_id = user_id;
        this.mUser_name = user_name;
        this.mUser_age = user_age;
        if (user_sex.equals("F")) {
            this.mUser_sex = "여";
        } else {
            this.mUser_sex = "남";
        }
        this.mUser_photo = user_photo;
        this.mMemo = memo;
        this.mTerm = date;
        this.mDate = "";
    }

    public int getBbs_id() { return mBbs_id; }
    public int getMemo_id() { return mBbs_memoid; }
    public int getUser_id() { return mUser_id; }
    public String getUser_name() { return mUser_name; }
    public int getUser_age() { return mUser_age; }
    public String getUser_sex() { return mUser_sex; }
    public String getUser_photo() { return mUser_photo; }
    public String getMemo() { return mMemo; }
    public String getTerm() { return mTerm; }
    public String getData() { return mDate; }

}