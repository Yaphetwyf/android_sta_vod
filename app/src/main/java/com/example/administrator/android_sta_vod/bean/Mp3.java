package com.example.administrator.android_sta_vod.bean;

/**
 * Created by AVSZ on 2017/1/10.
 */

public class Mp3 {
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
}
