package com.example.administrator.android_sta_vod.bean;

import android.os.Parcelable;

/**
 * Created by AVSZ on 2017/4/2.
 */

public class MusicBean extends FileBean implements Parcelable{
    private String length;
    private boolean select;

    public MusicBean(){

    }

    public MusicBean(String id, String pid, String name){
        super(id,pid,name);
    }

    public String getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    public boolean get_select()
    {
        return select;
    }

    public void set_select(boolean is_select)
    {
        this.select = is_select;
    }

    @Override
    public String toString()
    {
        return "MusicBean{" +
                "length=" + length +
                ", is_select=" + select +
                '}';
    }
}
