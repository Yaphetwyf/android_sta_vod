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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    @XStreamAsAttribute()
    @XStreamAlias("nick-name")
    private String nick_name;

    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    @XStreamAsAttribute()
    @XStreamAlias("rtsp")
    private String rtsp;
    public User(){

    }
    public User(String name,String nick_name,String status,String rtsp){
        this.name = name;
        this.status = status;
        this.nick_name=nick_name;
        this.rtsp=rtsp;
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
