package com.example.administrator.android_sta_vod.bean;

/**
 * Created by AVSZ on 2017/4/6.
 */

public class Path {
    private String path;
    private String length;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;


    public Path()
    {

    }

    public Path(String path, String length,String id)
    {
        this.id=id;
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
