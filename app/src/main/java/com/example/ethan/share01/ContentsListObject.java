package com.example.ethan.share01;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListObject
{
    private int _id;
    private int _user_id;
    private String _picUrl;
    private String _title;

    public ContentsListObject(int Id, int user_id, String PicUrl, String Title)
    {
        this._id = Id;
        this._user_id = user_id;
        this._picUrl = PicUrl;
        this._title = Title;
    }


    public int get_user_id() {
        return _user_id;
    }

    public void set_user_id(int _user_id) {
        this._user_id = _user_id;
    }
    public String getPicUrl()
    {
        return _picUrl;
    }

    public void setPicUrl(String PicUrl)
    {
        this._picUrl = PicUrl;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String Title)
    {
        this._title = Title;
    }

    public void setId(int Id) { this._id = Id; }

    public int getId() { return _id;}
}