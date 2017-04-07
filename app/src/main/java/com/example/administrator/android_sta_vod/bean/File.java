package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("file")
public class File {
    //文件路径
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    //文件时长
    @XStreamAsAttribute()
    @XStreamAlias("tm-long")
    private String tm_long;

    public File(String name, String tm_long){
        this.name = name;
        this.tm_long = tm_long;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTm_long() {
        return tm_long;
    }

    public void setTm_long(String tm_long) {
        this.tm_long = tm_long;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", tm_long='" + tm_long + '\'' +
                '}';
    }
}
