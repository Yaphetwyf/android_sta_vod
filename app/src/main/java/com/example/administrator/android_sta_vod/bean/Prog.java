package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("prog")
public class Prog {
    @XStreamAsAttribute()
    @XStreamAlias("path")
    private String path;
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    @XStreamAsAttribute()
    @XStreamAlias("ftm")
    private String length;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
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

    @Override
    public String toString() {
        return "Prog{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
