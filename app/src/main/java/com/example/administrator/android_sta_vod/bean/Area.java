package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("area")
public class Area {
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;
    @XStreamAsAttribute()
    @XStreamAlias("id")
    private String id;

    public Area(){

    }

    public Area(String name, String id){
        this.name = name;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Area{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
