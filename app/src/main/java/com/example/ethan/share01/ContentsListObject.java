package com.example.ethan.share01;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListObject
{
    private int _id;
    private String _picUrl;
    private String _title;

    public ContentsListObject(int Id, String PicUrl, String Title)
    {
        this._id = Id;
        this._picUrl = PicUrl;
        this._title = Title;
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