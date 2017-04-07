package com.example.administrator.android_sta_vod.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by AVSZ on 2016/12/26.
 */
@XStreamAlias("usr")
public class User {
    @XStreamAsAttribute()
    @XStreamAlias("name")
    private String name;

    @XStreamAsAttribute()
    @XStreamAlias("status")
    private String status;

    public User(){

    }
    public User(String name,String status){
        this.name = name;
        this.status = status;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
