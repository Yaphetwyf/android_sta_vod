package com.example.administrator.android_sta_vod.bean;

/**
 * Created by AVSZ on 2017/4/6.
 */

public class Path {
    private String path;
    private String length;

    public Path()
    {

    }

    public Path(String path, String length)
    {
        this.path = path;
        this.length = length;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    @Override
    public String toString()
    {
        return "Path{" +
                "path='" + path + '\'' +
                ", length='" + length + '\'' +
                '}';
    }
}
