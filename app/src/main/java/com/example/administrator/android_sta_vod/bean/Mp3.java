package com.example.administrator.android_sta_vod.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AVSZ on 2017/1/10.
 */

public class Mp3 implements Parcelable{
    private String path;
    private String name;
    private String length;

    public Mp3(){

    }
    public Mp3(String path, String name, String length){
        this.path = path;
        this.name = name;
        this.length = length;
    }

    protected Mp3(Parcel in)
    {
        path = in.readString();
        name = in.readString();
        length = in.readString();
    }

    public static final Creator<Mp3> CREATOR = new Creator<Mp3>() {
        @Override
        public Mp3 createFromParcel(Parcel in)
        {
            return new Mp3(in);
        }

        @Override
        public Mp3[] newArray(int size)
        {
            return new Mp3[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Mp3{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(length);
    }
}
